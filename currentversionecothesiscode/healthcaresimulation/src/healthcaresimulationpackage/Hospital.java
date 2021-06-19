package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
/**
 * needs deepcopy
 * @author miche
 *
 */
public class Hospital extends Localizedagent implements Deepcopiableactor 
{
	private int hosnum;
	private double price;
	private int capacity;
	public double costs;//costs per patient
	//private ArrayList<ArrayList<Double>>  pricechangeshistory;
	private  LinkedHashMap<Integer,ArrayList<ArrayList<Double>>> priceandcontractchanges; // records the price changes and rate changes
	//map outside is from insurer including non insured encoded as -1 to list of price changes 
	// inner arraylist of individual price change is: the price after each change and the time at which the change took effect. 
	//in the inner arraylist it is first the price and then the date at which it took effect
	public ArrayList<LinkedHashMap<Integer,Integer>> patienthistory;
	private LinkedHashMap<Integer,Integer> currentpatients;//maps patients to insurers
	public LinkedHashSet<Integer> contractedinsurers;
	private LinkedHashSet<Integer> allinsurers;//includes the no insurer option encoded as -1
	public LinkedHashMap<Integer,Double> insurerprices;
	private int rejectedpats;
	double profit;
	double fixedcosts;//costs per period regardless of number of patients
	ArrayList<Double> profithistory;
	Randomdraws qualdist;
	LinkedHashMap<Integer,ArrayList<Double>> conseqinschanges;//computational efficiency records previous changes
	public Hospital(Location loc, Government gov,double initprice,int initcap,double initcosts,
			Randomdraws qualdistin,double infixedcosts,int hosnumin, LinkedHashSet<Integer> allinsurersin)
	{
		super(loc,gov);
		price=initprice;
		capacity=initcap;
		costs=initcosts;
		priceandcontractchanges= new LinkedHashMap<Integer,ArrayList<ArrayList<Double>>>();
		patienthistory=new ArrayList<LinkedHashMap<Integer,Integer>>();
		currentpatients=new LinkedHashMap<Integer,Integer>();
		contractedinsurers=new LinkedHashSet<Integer>();
		rejectedpats=0;
		profit=0;
		qualdist=qualdistin;
		insurerprices=new LinkedHashMap<Integer,Double>();
		profithistory=new ArrayList<Double>();
		fixedcosts=infixedcosts;
		ArrayList<Double> pricechangedat=new ArrayList<Double>();
		pricechangedat.add(price);
		pricechangedat.add((double) 0);
		ArrayList<ArrayList<Double>> pricechangeshistory=new ArrayList<ArrayList<Double>>();
		pricechangeshistory.add(pricechangedat);
		priceandcontractchanges.put(-1, pricechangeshistory);
		hosnum=hosnumin;
		allinsurers=allinsurersin;
		for(int i:allinsurers)
		{
			if(i!=-1)
			{
				ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
				pricechangedatloc.add(price*2);
				pricechangedatloc.add((double) 0);
				ArrayList<ArrayList<Double>> pricechangeshistoryloc=new ArrayList<ArrayList<Double>>();
				pricechangeshistoryloc.add(pricechangedatloc);
				priceandcontractchanges.put(i, pricechangeshistoryloc);
			}
		}
	}
	/**
	 * 
	 * @param loc
	 * @param gov
	 * @param initprice
	 * @param initcap
	 * @param initcosts
	 * @param qualdistin
	 * @param infixedcosts
	 * @param hosnumin
	 * @param allinsurersin
	 * @param contractedinsurersandrates
	 */
	public Hospital(Location loc, Government gov,double initprice,int initcap,double initcosts,
			Randomdraws qualdistin,double infixedcosts,int hosnumin,
			LinkedHashSet<Integer> allinsurersin,
			LinkedHashMap<Integer,Double> contractedinsurersandrates)
	{
		super(loc,gov);
		price=initprice;
		capacity=initcap;
		costs=initcosts;
		priceandcontractchanges= new LinkedHashMap<Integer,ArrayList<ArrayList<Double>>>();
		patienthistory=new ArrayList<LinkedHashMap<Integer,Integer>>();
		currentpatients=new LinkedHashMap<Integer,Integer>();
		contractedinsurers=new LinkedHashSet<Integer>();
		rejectedpats=0;
		profit=0;
		qualdist=qualdistin;
		insurerprices=new LinkedHashMap<Integer,Double>();
		profithistory=new ArrayList<Double>();
		fixedcosts=infixedcosts;
		ArrayList<Double> pricechangedat=new ArrayList<Double>();
		pricechangedat.add(price);
		pricechangedat.add((double) 0);
		ArrayList<ArrayList<Double>> pricechangeshistory=new ArrayList<ArrayList<Double>>();
		pricechangeshistory.add(pricechangedat);
		priceandcontractchanges.put(-1, pricechangeshistory);
		hosnum=hosnumin;
		allinsurers=allinsurersin;
		for(int i:allinsurers)
		{
			if(i!=-1&&!contractedinsurersandrates.containsKey(i))
			{
				ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
				pricechangedatloc.add(price*2);
				pricechangedatloc.add((double) 0);
				ArrayList<ArrayList<Double>> pricechangeshistoryloc=new ArrayList<ArrayList<Double>>();
				pricechangeshistoryloc.add(pricechangedatloc);
				priceandcontractchanges.put(i, pricechangeshistoryloc);
			}
			else if(i!=-1&&!contractedinsurersandrates.containsKey(i))
			{
				ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
				pricechangedatloc.add(contractedinsurersandrates.get(i));
				pricechangedatloc.add((double) 0);
				ArrayList<ArrayList<Double>> pricechangeshistoryloc=new ArrayList<ArrayList<Double>>();
				pricechangeshistoryloc.add(pricechangedatloc);
				priceandcontractchanges.put(i, pricechangeshistoryloc);
				contractedinsurers.add(i);
				insurerprices.put(i, contractedinsurersandrates.get(i));


			}
		}
	}






	/**
	 * 	initializer for copies
	 */
	public Hospital(Location loc, Government gov, int inhosnum,double initprice,int initcap,double initcosts,
			LinkedHashMap<Integer,ArrayList<ArrayList<Double>>> inpriceandcontractchanges,
			ArrayList<LinkedHashMap<Integer,Integer>> inpatienthistory,
			LinkedHashMap<Integer,Integer> incurrentpatient, 
			LinkedHashSet<Integer>contractedinsurersin,LinkedHashMap<Integer,Double> insurerpricesin,
			int rejectedpatsin, double profitin,double infixedcosts,
			ArrayList<Double> profithistoryin,Randomdraws qualdistin, LinkedHashSet<Integer> allinsurersin)
	{
		super(loc,gov);
		hosnum=inhosnum;
		price=initprice;
		capacity=initcap;
		costs=initcosts;
		priceandcontractchanges=inpriceandcontractchanges;
		patienthistory=inpatienthistory;
		currentpatients=incurrentpatient;
		contractedinsurers=contractedinsurersin;
		insurerprices=insurerpricesin;
		rejectedpats=rejectedpatsin;
		profit=profitin;
		fixedcosts=infixedcosts;
		profithistory=profithistoryin;
		qualdist=qualdistin;
		allinsurers=allinsurersin;

	}

	/**
	 * 	Hospital(Location loc, Government gov, int inhosnum,double initprice,int initcap,double initcosts,
			ArrayList<ArrayList<Double>> inpricechangeshistory,
			ArrayList<LinkedHashMap<Integer,Integer>> inpatienthistory,
			LinkedHashMap<Integer,Integer> incurrentpatient, 
			LinkedHashSet<Integer>contractedinsurersin,LinkedHashMap<Integer,Double> insurerpricesin,
			int rejectedpatsin, double profitin,double infixedcosts,
			ArrayList<Double> profithistoryin,Randomdraws qualdistin)	
	 */
	public Hospital deepcopyagent() {
		Hospital newhospital=new Hospital(loc.deepcopy(),gov.deepcopyagent(),hosnum,price,capacity,costs,
				deepcopyclass.LHM_I_A_A_D(priceandcontractchanges),
				deepcopyclass.A_LHM_I_I(patienthistory),deepcopyclass.LHM_I_I(currentpatients),
				deepcopyclass.LHSI(contractedinsurers), deepcopyclass.LHM_I_D(insurerprices),
				rejectedpats,profit, fixedcosts,deepcopyclass.A_D(profithistory), qualdist.deepcopy(), allinsurers);
		return newhospital;
	}
	/**
	 * function that updates all neccesary values each period
	 */
	public void updateaftereverytimeperiod()
	{
		patienthistory.add(deepcopyclass.LHM_I_I(currentpatients));
		profit=profit-fixedcosts;
		//System.out.println("profit"+profit +"currentpatients"+currentpatients.size()+"hospital"+this.getID());
		profithistory.add(profit);
		profit=0;
		currentpatients.clear();
	}
	/**
	 * function that changes the price, it considers three prices  currentprice+1, currentprice-1
	 * and the best price based on the 
	 *  only call in beginning of period
	 */
	// finished
	public boolean changeprice() 
	{		
		ArrayList<ArrayList<Double>> pricestopriceinfo=this.calcnumpatsandtimes(-1,-1);
		double newpricesmallincrease=price+1;
		double newpricesmalldecrease=price-1;
		double maxprice=Double.MAX_VALUE;
		if(gov.ispricemaxed(hosnum))
		{
			maxprice=gov.getPricemax(hosnum);
		}
		double profitsmallincrease=0;
		double profitsmalldecrease=0;
		double heuroptprofit=0;
		double currentprofit=this.getcurrentavgprofit(-1);
		double bestprofit=-Double.MAX_VALUE;
		double bestprice=maxprice;
		if(price<=maxprice)
		{
			//inititaite the best profit and price as current if those are valid
			bestprofit=currentprofit;
			bestprice=price;

		}
		if(priceandcontractchanges.get(-1).size()==1&&patienthistory.size()>0)
		{
			//assumption is proportional changes  and changes only after observation period
			if(price>0)
			{

				double avgnumcurrentpats=pricestopriceinfo.get(0).get(1);// this is not correct.
				double	percchangessmallpos=(newpricesmallincrease-price)/price;
				double posincpat=(1-percchangessmallpos)*avgnumcurrentpats;
				if(posincpat>capacity)
				{
					posincpat=capacity;
				}
				profitsmallincrease=posincpat*(newpricesmallincrease-costs)-fixedcosts;
				//System.out.println("numpats: "+avgnumcurrentpats+"variable costs: "+ costs+"fixed costs: "+fixedcosts+"price: "+price+"currentprofit: "+currentprofit);
				if(profitsmallincrease>bestprofit&&newpricesmallincrease<=maxprice)
				{
					bestprice=newpricesmallincrease;
					bestprofit=profitsmallincrease;
				}

				double	percchangessmallneg=(newpricesmalldecrease-price)/price;
				double negincpat=(1-percchangessmallneg)*avgnumcurrentpats;
				if(avgnumcurrentpats<(double)capacity*0.20)
				{
					negincpat=negincpat+1;
				}
				if(negincpat>capacity)
				{
					negincpat=capacity;
				}
				profitsmalldecrease=negincpat*(newpricesmalldecrease-costs)-fixedcosts;
				if(profitsmalldecrease>bestprofit&&newpricesmalldecrease>costs)
				{
					bestprice=newpricesmalldecrease;
					bestprofit=profitsmalldecrease;
				}
				double heuroptprice=price+costs/2;//see hopsitaltheory doc in overleaf fro calculation
				double	percchangesheuropt=(heuroptprice-price)/price;
				double heuroptpat=(1-percchangesheuropt)*avgnumcurrentpats;
				heuroptprofit=heuroptpat*(heuroptprice-costs)-fixedcosts;
				if(heuroptpat>capacity)
				{
					heuroptpat=capacity;
				}
				if(heuroptprofit>bestprofit&&heuroptprice<=maxprice)
				{
					bestprice=heuroptprice;
					bestprofit=heuroptprofit;
				}
			}
			else //if(maxprice>0) not neccesary
			{
				if(maxprice>=costs+1)
				{
					bestprice=costs+1;
				}
				else
				{
					bestprice=maxprice;
				}
				bestprofit=1;
			}
		}
		else
		{
			currentprofit=this.calcprofitforprice(-1, price);
			double histoptprice=this.gethistoricopt();
			double histoptprofit=this.calcprofitforprice(-1, histoptprice);
			if(histoptprofit>bestprofit&&histoptprice<=maxprice&&histoptprice>costs)
			{
				bestprice=histoptprice;
				bestprofit=histoptprofit;
			}
			newpricesmallincrease= price+price*0.01;
			if(newpricesmallincrease<=0||newpricesmallincrease==price)
			{
				newpricesmallincrease= price+1;

			}
			double smallincreaseprofit=this.calcprofitforprice(-1, newpricesmallincrease);
			if(smallincreaseprofit>bestprofit&&newpricesmallincrease<=maxprice)
			{
				bestprice=newpricesmallincrease;
				bestprofit=smallincreaseprofit;
			}
			newpricesmalldecrease=price*0.99;
			double smalldecreaseprofit=this.calcprofitforprice(-1, newpricesmalldecrease);
			if(smalldecreaseprofit>bestprofit&&newpricesmalldecrease<=maxprice&&newpricesmalldecrease>costs)
			{
				bestprice=newpricesmalldecrease;
				bestprofit=smalldecreaseprofit;
			}
			double LIprice=price+price*0.1;
			if(LIprice<=0||LIprice==price)
			{
				LIprice=price+10;
			}
			double LIprofit=this.calcprofitforprice(-1, LIprice);

			if(LIprofit>bestprofit&&LIprice<=maxprice)
			{
				bestprice=LIprice;
				bestprofit=LIprofit;
			}
			double LDprice=price*0.9;
			double LDprofit=this.calcprofitforprice(-1, LDprice);
			if(LDprofit>bestprofit&&LDprice<=maxprice&&LDprice>costs)
			{
				bestprice=LDprice;
				bestprofit=LDprofit;
			}
			//this.numpats
			// This is not current anymore  it is saved for posterity
			//		double timemultiplier=0;
			//			for(int i=1;i<pricechangeshistory.size();i++)
			//			{
			//				timemultiplier=timemultiplier+currenttime-pricechangeshistory.get(i-1).get(1);
			//			}
			//			for(int i=1;i<pricechangeshistory.size();i++)
			//			{
			//				double prevprice=pricechangeshistory.get(i-1).get(0);
			//				double prevnumpatients=pricechangeshistory.get(i-1).get(1);
			//				double historicprice=pricechangeshistory.get(i).get(0);
			//				double historicnumpatients=pricechangeshistory.get(i).get(1);
			//				int fmultiplier=-1;
			//				if((price>optprice&&prevprice>historicprice)||(price<optprice&&prevprice<historicprice))
			//				{
			//					fmultiplier=1;
			//				}
			//			}
			//			if(optprice>0)
			//			{
			//		}
		}
		if(bestprice>maxprice&&price<=maxprice)
		{
			bestprofit=-Double.MAX_VALUE;
		}
		else if(bestprice>maxprice&&price>maxprice)
		{
			bestprice=maxprice;
			bestprofit=Double.MAX_VALUE;
		}
		boolean changed=false;
		if(bestprofit>currentprofit&&bestprice!=price)
		{
			changed=true;
			price=bestprice;
			ArrayList<Double> pricechangedat=new ArrayList<Double>();
			pricechangedat.add(bestprice);
			pricechangedat.add((double) patienthistory.size());
			priceandcontractchanges.get(-1).add(pricechangedat);
			//System.out.println("the price WAS CHANGED");
			for(int i: allinsurers)
			{
				if(i!=-1&&!contractedinsurers.contains(i))
				{
					ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
					pricechangedatloc.add(bestprice*2);
					pricechangedatloc.add((double) patienthistory.size());
					priceandcontractchanges.get(i).add(pricechangedatloc);
				}
			}
		}
		return changed;
	}
	/**
	 * this function gives the price for which the average historic profit was highest
	 * 
	 * @return
	 */
	private double gethistoricopt() {
		int currenttime=patienthistory.size();
		int newtime=0;
		double histoptprice=price;
		double optprofit=-Double.MAX_VALUE;
		for(int j=0;j<priceandcontractchanges.get(-1).size();j++)
		{
			int oldtime=newtime;
			if(j<priceandcontractchanges.get(-1).size()-1)
			{
				newtime=(int) Math.round(priceandcontractchanges.get(-1).get(j+1).get(1));
			}
			else
			{
				newtime=currenttime;
			}
			double totaltime=0;
			double totalprofit=0;			
			for(int i=oldtime;i<newtime;i++)
			{
				totalprofit=profithistory.get(i);
				totaltime++;
			}
			double avgprofit=totalprofit/totaltime;
			if(avgprofit>optprofit)
			{
				optprofit=avgprofit;
				histoptprice=priceandcontractchanges.get(-1).get(j).get(0);
			}
		}
		return histoptprice;
	}
	private double getcurrentavgprofit(int ins) {
		int pricechangetime=0;
		if(priceandcontractchanges.get(ins).size()>0)
		{
			pricechangetime=(int) Math.round(priceandcontractchanges.get(ins).get(priceandcontractchanges.get(ins).size()-1).get(1));
		}
		double totaltime=0;
		double totalprofit=0;
		for(int i=pricechangetime;i<profithistory.size();i++)
		{
			totalprofit=totalprofit+profithistory.get(i);
			totaltime++;
		}
		double avgprofit=totalprofit/totaltime;

		return avgprofit;
	}
	/**
	 * this function takes the prices and relates them to the average number of
	 *  noninsured patients and the number of time periods this price 
	 *  was in place until it was changed again
	 *  entries in inner arraylist are : price, average numcustomers, num time period
	 *  uses pricechangeshistory and patienthistory
	 * @return
	 */
	public ArrayList<ArrayList<Double>> calcnumpatsandtimes(int pricechangeins, int numpatsins) {
		//  not done check weird results
		ArrayList<ArrayList<Double>> numpricespatsandtimes=new ArrayList<ArrayList<Double>>();
		int currenttime=patienthistory.size();
		//		if(currenttime==1000)
		//		{
		//			int counter=0;
		//			for(int i=10;i<20;i++)
		//			{
		//				counter=counter+patienthistory.get(i).size();
		//			}
		//			System.out.println("counter"+counter+"hosnum"+hosnum);
		//		}
		int newtime=0;
		if(priceandcontractchanges.get(pricechangeins).size()>0)
		{
			for(int j=0;j<priceandcontractchanges.get(pricechangeins).size();j++)
			{
				double price=priceandcontractchanges.get(pricechangeins).get(j).get(0);
				int oldtime=newtime;
				if(j<priceandcontractchanges.get(pricechangeins).size()-1)
				{
					newtime=(int) Math.round(priceandcontractchanges.get(pricechangeins).get(j+1).get(1));
				}
				else
				{
					newtime=currenttime;
				}
				double numpatients=0;
				for(int i=oldtime;i<newtime;i++)
				{
					for (Entry<Integer, Integer> visit : patienthistory.get(i).entrySet())
					{
						if(visit.getValue()==numpatsins)
						{
							numpatients++;
						}
					}
				}
				ArrayList<Double> stats=new ArrayList<Double>();
				double numtimeperiods=newtime-oldtime;
				stats.add(price);
				if(numtimeperiods>0)
				{
					stats.add(numpatients/numtimeperiods);
				}
				else
				{
					stats.add((double) 0);
				}
				stats.add(numtimeperiods);
				numpricespatsandtimes.add(stats);
			}
		}

		return numpricespatsandtimes;
	}
	/**
	 * deprecated not that usefull.
	 * @return
	 */
	public double getnumnoninsuredpatientslasttenperiods() {
		int numuninsuredpatients=0;
		for(int i=patienthistory.size()-1;i>patienthistory.size()-11&&i>=0;i=i-1)
		{
			for (Entry<Integer, Integer> visit : patienthistory.get(i).entrySet())
			{
				if(visit.getValue()<0)
				{
					numuninsuredpatients++;
				}
			}
		}
		return numuninsuredpatients;
	}
	public boolean processpatient(Patient patient)
	{
		boolean hasenoughmoney=false;
		boolean accepted=true;
		if(contractedinsurers.contains(patient.getinsurer()))
		{
			hasenoughmoney=patient.getcurrentmoney()>=patient.getspecrealcosts(this.getID());
		}
		else
		{
			hasenoughmoney=patient.getcurrentmoney()>=price;
		}
		if(patient.getID()==412)
		{
			//			System.out.println("currentmoney: "+patient.getcurrentmoney());
			//			System.out.println("currentprice: "+price);
			//			System.out.println("currenttimeperiod: "+this.patienthistory.size());
		}
		if(currentpatients.size()>=capacity||!hasenoughmoney)
		{
			patient.updatepatientaftervisit(false, 0, price, hosnum, false);
			rejectedpats++; 
			accepted=false;
			//test code
			if(hosnum==1&&!hasenoughmoney&&currentpatients.size()>=capacity)
			{
				//System.out.println("both are the problem");
			}
			else if(hosnum==1&&!hasenoughmoney)
			{
				//System.out.println("price is the problem");

				if(this.getrecentpatients().contains(patient.getID()))
				{
				}
				else
				{

				}
			}
			else if(hosnum==1&&currentpatients.size()>=capacity)
			{
				//System.out.println("capacity is the problem");
			}
			// System.out.println("patient "+patient.getID()+" has "+ patient.getcurrentmoney()+"which is insufficient");
		}
		else
		{
			currentpatients.put(patient.getID(),patient.getinsurer());
			if(patient.getID()==526&&this.getID()==0)
			{
				//System.out.println("insurers"+contractedinsurers);
			}
			double quality=qualdist.draw() ;
			//System.out.println("pat currmoney"+patient.getcurrentmoney());
			//double beforemoney=patient.getcurrentmoney();
			patient.updatepatientaftervisit(true,quality, price, hosnum, false);
			//System.out.println("pat new  currmoney"+patient.getcurrentmoney());
			gov.updatehosinfo(quality, price, hosnum);
			//System.out.println("hos prof"+profit+"pirce"+price+"costs"+costs);
			if(contractedinsurers.contains(patient.getinsurer()))
			{
				//System.out.println("pat before money"+beforemoney);
				//System.out.println("hos profit before"+profit);
				profit=profit+(insurerprices.get(patient.getinsurer())-costs);
				//System.out.println("hos profit after"+profit);
				//System.out.println("pat new  currmoney"+patient.getcurrentmoney());

			}
			else
			{
				profit=profit+(price-costs);
			}
			//System.out.println("hos prof after"+profit);


		}
		return accepted;
	}
	@Override
	public int getID()
	{
		return hosnum;
	}
	public int getnumrejectpats()
	{
		return rejectedpats;
	}
	public void addinsurer(int insurer, double compensation) {
		contractedinsurers.add(insurer);
		insurerprices.put(insurer, compensation);

	}
	/**
	 *  this funciton determines if a hospital accepts an insurers offer
	 * @param insid
	 * @param rate
	 * @param weightednumnewposclients
	 * @return
	 *  this might not work as intended the added profit and lost profit calculations might be off.
	 */
	public boolean acceptinsureroffer(int insid, double rate,double fracnearbypatient)
	{
		boolean accept=false;
		if(rate<costs)
		{
		}
		else
		{
			if(this.howmanypriorcontracts(insid)>=2)
			{
				double	newprofitest=this.calcprofitforprice(insid, rate);
				double 	currentprofitest=this.calcprofitforprice(insid,insurerprices.get(insid));
				if(newprofitest>currentprofitest)
				{
					accept=true;
				}
			}
			else
			{
				if(contractedinsurers.size()>0)
				{
					boolean highestrate=true;
					for(int i:insurerprices.keySet())
					{
						if(insurerprices.get(i)<=rate&&contractedinsurers.contains(i))
						{
							highestrate=false;
						}
					}
					if(highestrate)
					{
						accept=true;
					}
				}
				if(rate==price-0.01)
				{
					accept=true;
				}
				double totalnumpats=0;
				//this.getrecentpatients();
				for(int i=0;i<30;i++)
				{
					if(patienthistory.size()>=1+i)
					{
					totalnumpats=totalnumpats+patienthistory.get(patienthistory.size()-1-i).keySet().size();
				
					}
				}
				double relnumpats=totalnumpats/30;
				double relpats=relnumpats/(double) capacity;
				double minprice=costs+fixedcosts/(double) capacity;
				double prop=(rate-minprice)/(price-minprice);
				if((1-prop)<fracnearbypatient||(relpats<0.5&&(1-prop)<2*fracnearbypatient))
				{
					accept=true;
				}

			}

		}
		if(accept)
		{
			ArrayList<Double> vals=new ArrayList<Double>();
			vals.add(rate);
			vals.add((double) patienthistory.size());
			vals.add((double) 1);
			if(priceandcontractchanges.containsKey(insid))
			{
				priceandcontractchanges.get(insid).add(vals);
			}
			else
			{
				ArrayList<ArrayList<Double>> entry=new ArrayList<ArrayList<Double>>();
				entry.add(vals);
				priceandcontractchanges.put(insid,entry);
			}
			this.addinsurer(insid,rate);

		}
		return accept;
	}
	private int howmanypriorcontracts(int ins) {
		int numcontracts=0;
		if(priceandcontractchanges.containsKey(ins))
		{
			for(int i=0;i<priceandcontractchanges.get(ins).size();i++)
			{
				if(priceandcontractchanges.get(ins).get(i).size()>2)
				{
					numcontracts++;
				}
			}
		}
		return numcontracts;
	}
	public LinkedHashMap<Integer,Boolean> insurersstillworth()
	{
		LinkedHashMap<Integer,Boolean> stillworthvals=new LinkedHashMap<Integer,Boolean>();
		for(int i:allinsurers)
		{
			if(i!=-1&&contractedinsurers.contains(i))
			{
				if(priceandcontractchanges.get(i).size()>1)
				{
					double projectedcurrentprofit=this.calcprofitforprice(i, insurerprices.get(i));
					double projectednoinsprofit=this.calcprofitforprice(i, price*2);
					if(projectednoinsprofit>projectedcurrentprofit)
					{
						stillworthvals.put(i,false);
						ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
						pricechangedatloc.add(price*2);
						pricechangedatloc.add((double) 0);
						priceandcontractchanges.get(i).add(pricechangedatloc);
						contractedinsurers.remove(i);
						insurerprices.remove(i);
					}
					else
					{
						stillworthvals.put(i,true);

					}
				}
				else
				{
					double minprice=costs+fixedcosts/(double) capacity;
					double frac=(insurerprices.get(i)-minprice)/(price-minprice);
					double totalnumpats=0;
					for(int j=0;j<30;j++)
					{
						totalnumpats=totalnumpats+patienthistory.get(patienthistory.size()-1).keySet().size();
					}
					if(2*totalnumpats/30>capacity)
					{
						if(this.calcnumpatsandtimes(i, i).get(0).get(1)/((double) capacity)<1-frac)
						{
							stillworthvals.put(i,false);
							ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
							pricechangedatloc.add(price*2);
							pricechangedatloc.add((double) 0);
							priceandcontractchanges.get(i).add(pricechangedatloc);
							contractedinsurers.remove(i);
							insurerprices.remove(i);
						}
					}
					else
					{
						if(2*this.calcnumpatsandtimes(i, i).get(0).get(1)/((double) capacity)<1-frac)
						{
							stillworthvals.put(i,false);
							ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
							pricechangedatloc.add(price*2);
							pricechangedatloc.add((double) 0);
							priceandcontractchanges.get(i).add(pricechangedatloc);
							contractedinsurers.remove(i);
							insurerprices.remove(i);
						}
					}
				}
			}
		}

		return stillworthvals;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<LinkedHashMap<Integer,Integer>> getpatienthistory()
	{
		return patienthistory;
	}
	public double getprice()
	{
		return price;
	}
	public LinkedHashMap<Integer,Integer> getcurrentpatients()
	{
		return currentpatients;
	}
	/**
	 * deprecated
	 * gets all patients that visited since ten periods ago
	 * @return
	 */
	public LinkedHashSet<Integer> getrecentpatients() {
		LinkedHashSet<Integer> recentpatients=new LinkedHashSet<Integer>();
		for(int i=patienthistory.size()-1;i>patienthistory.size()-11&&i>=0;i=i-1)
		{
			recentpatients.addAll(patienthistory.get(i).keySet());
		}
		return recentpatients;
	}
	/**
	 * deprecated problably unneccesary
	 * @param tbegin
	 * @param tend
	 * @param prevbegin
	 * @return
	 */
	public LinkedHashMap<Integer,Double> numpatschangeperperiod(int tbegin, int tend, int prevbegin)
	{
		LinkedHashMap<Integer,Integer> prevnumpatsperins=new LinkedHashMap<Integer,Integer>();
		for(int i=prevbegin;i<tbegin;i++ )
		{
			LinkedHashMap<Integer,Integer> patsthisperiod=patienthistory.get(i);
			//no insurer is encode as -1
			for(Entry<Integer,Integer> patrec:patsthisperiod.entrySet())
			{
				int pat=patrec.getKey();
				int ins=patrec.getValue();
				if(!prevnumpatsperins.containsKey(pat))
				{
					prevnumpatsperins.put(ins, 1);
				}
				else
				{
					prevnumpatsperins.put(ins, prevnumpatsperins.get(ins));
				}
			}
		}
		LinkedHashMap<Integer,Integer> numpatsperins=new LinkedHashMap<Integer,Integer>();
		for(int i=tbegin;i<tend;i++ )
		{
			LinkedHashMap<Integer,Integer> patsthisperiod=patienthistory.get(i);
			//no insurer is encode as -1
			for(Entry<Integer,Integer> patrec:patsthisperiod.entrySet())
			{
				int pat=patrec.getKey();
				int ins=patrec.getValue();
				if(!numpatsperins.containsKey(pat))
				{
					numpatsperins.put(ins, 1);
				}
				else
				{
					numpatsperins.put(ins, numpatsperins.get(ins));
				}
			}
		}
		LinkedHashMap<Integer,Double> netchangestoinspats=new LinkedHashMap<Integer,Double>();
		for(int i:allinsurers)
		{
			double originalval=0;
			double newval=0;
			if(prevnumpatsperins.containsKey(i))
			{
				originalval=((double)prevnumpatsperins.get(i))/((double)tbegin-prevbegin);
			}
			if(numpatsperins.containsKey(i))
			{
				newval=((double)numpatsperins.get(i))/((double)tend-tbegin);
			}
			netchangestoinspats.put(i, newval-originalval);
		}
		return netchangestoinspats;
	}
	public LinkedHashMap<Integer,Integer> getnumpatsofinsforprice(int tbegin, int tend)
	{
		LinkedHashMap<Integer,Integer> numpatsperins=new LinkedHashMap<Integer,Integer>();
		for(int i=tbegin;i<tend;i++ )
		{
			LinkedHashMap<Integer,Integer> patsthisperiod=patienthistory.get(i);
			//no insurer is encode as -1
			for(Entry<Integer,Integer> patrec:patsthisperiod.entrySet())
			{
				int pat=patrec.getKey();
				int ins=patrec.getValue();
				if(!numpatsperins.containsKey(pat))
				{
					numpatsperins.put(ins, 1);
				}
				else
				{
					numpatsperins.put(ins, numpatsperins.get(ins));
				}
			}
		}
		return numpatsperins;
	}
	public double calcprofitforprice( 
			int considerdins, double newprice)
	{
		ArrayList<ArrayList<Double>> pricenumpatstime=this.calcnumpatsandtimes(considerdins, considerdins);
		Matrix X=Matrix.constructWLSXhos(pricenumpatstime);
		Matrix thisinsY=Matrix.constructWLSYhos(pricenumpatstime);
		int currenttime=patienthistory.size();
		Matrix W=Matrix.constructWLSWhos(pricenumpatstime, currenttime, newprice);
		LinkedHashMap<Integer,Matrix> allYs=new LinkedHashMap<Integer,Matrix>();
		allYs.put(considerdins, thisinsY);
		for(int i:allinsurers)
		{
			if(priceandcontractchanges.containsKey(i))
			{
				allYs.put(i, Matrix.constructWLSYhos(this.calcnumpatsandtimes(considerdins,i)));
			}
		}
		LinkedHashMap<Integer,Double> patests=new LinkedHashMap<Integer,Double>();
		double totalnumpats=0;
		for(int i: allYs.keySet())
		{
			ArrayList<Double> multipliers=Matrix.WLS(X, allYs.get(i), W);
			double numpatest=multipliers.get(0)+multipliers.get(1)*newprice;
			patests.put(i, numpatest);
			totalnumpats=totalnumpats+numpatest;
		}
		double capadj=1;
		if(totalnumpats>capacity)
		{
			capadj=((double)capacity)/((double) totalnumpats);
		}
		double locprofit=0;
		//System.out.println("new numpats: "+totalnumpats);

		for(int i:patests.keySet())
		{
			if(i==-1&&considerdins!=-1)
			{
				locprofit=totalnumpats*capadj*(price-costs);
			}
			else if(i!=considerdins)
			{

				if(contractedinsurers.contains(i))
				{
				locprofit=totalnumpats*capadj*(insurerprices.get(i)-costs);
				}
				else if(considerdins!=-1)
				{
					locprofit=totalnumpats*capadj*(price-costs);

				}
				else if(considerdins==-1)
				{
					locprofit=totalnumpats*capadj*(newprice-costs);
				}
			}
			else
			{
				locprofit=totalnumpats*capadj*(newprice-costs);

			}
		}
		locprofit=locprofit-fixedcosts;
		return locprofit;
	}
	public int numnonuniquerecentpats()
	{
		int numnonunique=0;
		for(int i=patienthistory.size()-1;i>patienthistory.size()-11&&i>=0;i=i-1)
		{
			numnonunique=numnonunique+patienthistory.get(i).keySet().size();
		}
		return numnonunique;
	}
	public double getpatstddev() {
		LinkedHashMap<Integer,Double> patmap=new LinkedHashMap<Integer,Double>();
		for(int i=patienthistory.size()-1;i>patienthistory.size()-365&&i>=0;i=i-1)
		{
			for(int j:patienthistory.get(i).keySet())
			{
				if(patmap.containsKey(j))
				{
					patmap.put(j, patmap.get(j)+1);
				}
				else
				{
					patmap.put(j, (double) 1);
				}
			}
		}
		double meanqual=0;
		for(int i:patmap.keySet())
		{
			meanqual=meanqual+patmap.get(i);
		}
		meanqual=meanqual/patmap.keySet().size();
		double sumofsquares=0;	
		for(int i:patmap.keySet())
		{
			sumofsquares=sumofsquares+Math.pow(patmap.get(i),2);
		}

		double stddiff=Math.pow(sumofsquares/patmap.keySet().size()-Math.pow(meanqual,2),0.5);

		return stddiff;
	}
	public double getmeanpats(int periodlength) {
		LinkedHashMap<Integer,Double> patmap=new LinkedHashMap<Integer,Double>();
		for(int i=patienthistory.size()-1;i>patienthistory.size()-periodlength&&i>=0;i=i-1)// this period needs to be variable
		{
			for(int j:patienthistory.get(i).keySet())
			{
				if(patmap.containsKey(j))
				{
					patmap.put(j, patmap.get(j)+1);
				}
				else
				{
					patmap.put(j, (double) 1);
				}
			}
			//System.out.println("runs"+i);
		}
		//System.out.println(patmap);
		double meanqual=0;
		for(int i:patmap.keySet())
		{
			meanqual=meanqual+patmap.get(i);
		}
		meanqual=meanqual/patmap.keySet().size();
		return meanqual;
	}
	public double gettotalprofit()
	{
		double totalprofit=0;
		for(int i=0;i<profithistory.size();i++)
		{
			totalprofit=totalprofit+profithistory.get(i);
		}
		return totalprofit;
	}
	public void setcap(int cap)
	{
		capacity=cap;
	}
	public void removeinsurer(int ins, int time)
	{
		contractedinsurers.remove(ins);
		insurerprices.remove(ins);	
		ArrayList<Double> pricechangedatloc=new ArrayList<Double>();
		pricechangedatloc.add(price*2);
		pricechangedatloc.add((double) time);
		ArrayList<ArrayList<Double>> pricechangeshistoryloc=new ArrayList<ArrayList<Double>>();
		pricechangeshistoryloc.add(pricechangedatloc);
		priceandcontractchanges.put(ins, pricechangeshistoryloc);
	}
	public LinkedHashSet<Integer> getrecentpatsforfrac()
	{
		LinkedHashSet<Integer> pats=new LinkedHashSet<Integer>();
		for(int i=patienthistory.size()-1;i>patienthistory.size()-183&&i>=0;i=i-1)
		{
			pats.addAll(patienthistory.get(i).keySet());
		}
		return pats;
	}
}
