package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.distribution.NormalDistribution;
/**
 * still to be done insurer offer to hospitals, insurer decision to keep or not keep hospital 
 * decision to keep patients
 * @author michel Hofmeijer 
 * for expected value calculation see https://stats.stackexchange.com/questions/166273/expected-value-of-x-in-a-normal-distribution-given-that-it-is-below-a-certain-v
 * and adjust for larger then
 *
 */
public class Insurer extends Restrictableactor implements Deepcopiableactor {
	private LinkedHashSet<Integer> patients;
	public LinkedHashMap<Integer,Double> patientrates;//rates per patients
	private LinkedHashSet<Integer> hospitals;
	// private LinkedHashMap<Integer, Double>  
	private boolean needmorepatients;//variable to indicate insurer will make more generous offers to patients and hospitals to get more contracts
	private LinkedHashMap<Integer,Double> prices; //the prices that the patient has to pay per hospital if they visit
	private LinkedHashMap<Integer,ArrayList<Integer>> patientvisits;
	private int insurernum;
	private double rate;
	private LinkedHashMap<Integer,Double> costs;// the costs(without taking the money the patient pays into account) that the insurer pays when a insured patient visits a hospital
	private boolean fixedrate;//if the rates per patient are fixed
	private double coverrate;// fraction of the costs the insurer covers
	private double profit; //profit
	private double baseET;// basic assumption of how often patients go to the hospital
	private double profitmulitplier;
	private double time;//just the time 
	private  double deductible;// the deductibles paid by patients in their first few visits to hospitals fixed per hos
	private  LinkedHashMap<Integer,Double> deductiblespaid;//the amount of deductibles paid already in the current period
	private  int deductibleresettimer;//number of periods left in the deductible 
	private int deductibleresetperiod;
	private int ratepayperiod;
	private int ratepaytimer;
	private LinkedHashMap<Integer,Integer> lasthospitalperpat;// the last hospital a patient has visited
	private LinkedHashMap<Integer,Integer> numvisitsperpatient;// number of visits per patient;
	private LinkedHashMap<Integer,Integer> numperiodsinf;//number of periods there is information for
	private LinkedHashMap<Integer, Integer> numvisitsperpatientold;// number of visits per patient before break in pattern
	private LinkedHashMap<Integer, Integer> numperiodsinfold;// number of 
	private int pertoirrel;//number periods until old data becomes irrelevant
	private double oldinfomult;// how much this insurer values what it believes to be old info
	private ArrayList<Double> profithistory;
	private double goalprofit;//absolute amount of profit the insurer aims to at least have.
	public Insurer(Government ingov,double baseETin,double baserate, LinkedHashMap<Integer,Double> inpatrates,
			LinkedHashMap<Integer,Double> incosts, int inratepayperiod,
			int ininsurernum, boolean fixedratein, double coverratein, double inprofitmultiplier,
			double indeductible,int indeductibleresetperiod, int pertoirrelin,
			double inoldinfomult, double ingoalprofit) {
		super(ingov);
		// TODO Auto-generated constructor stub
		patients=new LinkedHashSet<Integer>();
		rate=baserate;
		insurernum=ininsurernum;
		fixedrate=fixedratein;
		coverrate=coverratein;
		deductible=indeductible;
		profitmulitplier=inprofitmultiplier;
		patientvisits=new LinkedHashMap<Integer,ArrayList<Integer>>();
		patientrates=inpatrates;
		patients=new LinkedHashSet<Integer>();
		patients.addAll(inpatrates.keySet());
		numvisitsperpatient=new LinkedHashMap<Integer, Integer> ();
		numperiodsinf=new LinkedHashMap<Integer, Integer> ();
		numvisitsperpatientold=new LinkedHashMap<Integer, Integer> ();
		numperiodsinfold=new LinkedHashMap<Integer, Integer> ();
		ratepaytimer=inratepayperiod-1;
		//System.out.println("org val ratepaytimer"+ratepaytimer);
		ratepayperiod=inratepayperiod;
		deductiblespaid=new LinkedHashMap<Integer,Double>();
		for(int i: patients)
		{
			ArrayList<Integer> visitlog=new ArrayList<Integer>();
			patientvisits.put(i,visitlog);
			numvisitsperpatient.put(i, 0);
			numperiodsinf.put(i, 0);
			numvisitsperpatientold.put(i, 0);
			numperiodsinfold.put(i, 0);
			deductiblespaid.put(i, (double) 0);
		}

		costs=incosts;
		hospitals=new LinkedHashSet<Integer>();
		hospitals.addAll(costs.keySet());
		prices=new LinkedHashMap<Integer,Double>();

		for(int i:hospitals)
		{
			prices.put(i, costs.get(i)*coverrate);
		}
		profit=0;
		baseET=baseETin;
		time=0;
		lasthospitalperpat=new LinkedHashMap<Integer,Integer>();
		profithistory=new ArrayList<Double>();
		needmorepatients=false;
		deductibleresettimer=0;
		pertoirrel=pertoirrelin;
		oldinfomult=inoldinfomult;
		deductibleresetperiod=indeductibleresetperiod;
		goalprofit=ingoalprofit;

	}

	@Override
	public Insurer deepcopyagent() {
		// TODO Auto-generated method stub
		return null;
	}
	public LinkedHashSet<Integer> getpatients()
	{
		return patients;
	}
	public LinkedHashSet<Integer> gethospitals()
	{
		return hospitals;
	}
	public LinkedHashMap<Integer,Double> getprices()
	{
		return prices;
	}
	public double getbaserate()
	{
		return rate;
	}
	@Override
	public int getID()
	{
		return insurernum;
	}
	//should be finished
	/**
	 * this function is only for the intial offer 
	 * decisions to keep patient insured or subsequent offers are handled elsewhere
	 * the number of rejections indicates if a patient 
	 * @param p
	 * @return
	 */
	public double patientoffer(Patient p, int numrejections)
	{
		double offeredrate=-10;
		double locET=0;
		boolean feasibleoffer=true;
		int ID=p.getID();
		if(!gov.providesinfoonpattoins(insurernum,p.getID())&&!numvisitsperpatient.containsKey(ID))
		{
			locET=baseET;
			if(Double.isNaN(locET))
			{
				System.out.println(" base locET problem");
			}
			//System.out.println("check1"+locET);
		}
		else if(!numvisitsperpatient.containsKey(ID))
		{
			locET=gov.getpatientETinfospec(ID);
			if(Double.isNaN(locET))
			{
				System.out.println(" govlocET problem");
			}
			//System.out.println("check2");
			//System.out.println(this.getpatients().contains(p.getID())+" "+p.getinsurer());

		}
		else 
		{
			locET=this.getpatET(p.getID());
			if(Double.isNaN(locET))
			{
				System.out.println("  own inf locET problem");
			}
			//System.out.println("check3");

		}

		double mean=deductibleresetperiod*locET;
		//	System.out.println("mean"+mean);
		double std=Math.pow(deductibleresetperiod*locET*(1-locET),0.5);

		NormalDistribution normdist=new NormalDistribution(mean,0.001);
		if(std>0.001)
		{
			normdist=new NormalDistribution(mean,std);
		}
		if(fixedrate&&gov.obligationinsurance(insurernum))
		{
			offeredrate=rate;
			if(offeredrate>gov.getmaxrateins(insurernum))
			{ 
				offeredrate=gov.getmaxrateins(insurernum);
			}
		}
		else if(fixedrate)
		{
			double averagecosts=this.getavgcosts();
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			double frac=deductible/averagecosts;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			if(profitmulitplier*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc)<=rate*((double)deductibleresetperiod /ratepayperiod)||(needmorepatients&&(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc)<=rate*((double)deductibleresetperiod /ratepayperiod)))
			{
				offeredrate=rate;

			}
			else
			{
				feasibleoffer=false;
			}
			if(offeredrate>gov.getmaxrateins(insurernum))
			{

				feasibleoffer=false;
			}
		}
		else if(gov.obligationinsurance(insurernum))
		{
			double averagecosts=this.getavgcosts();
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			double frac=deductible/averagecosts;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			offeredrate=profitmulitplier*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));
			if(numrejections==0)
			{
				offeredrate=(profitmulitplier+(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(numrejections==1&&needmorepatients)
			{
				offeredrate=(profitmulitplier-(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			else if(numrejections==2&&needmorepatients)
			{
				offeredrate=(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(mean==0)
			{
				offeredrate=rate;
			}
			if(offeredrate>gov.getmaxrateins(insurernum))
			{
				offeredrate=gov.getmaxrateins(insurernum);
				feasibleoffer=true;
			}

		}
		else
		{
			double averagecosts=this.getavgcosts();
			double frac=deductible/averagecosts;
			//System.out.println("avgcosts"+averagecosts);
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			//TODO frac should be deducted from expectedvalgivenabovededuc since it is not eh number of visits
			//given that it is higher than the deductible it is that minus the number of visits used to reach the deductible
			offeredrate=profitmulitplier*(deducprob*coverrate*averagecosts*(expectedvalgivenabovededuc)*((double)ratepayperiod  /deductibleresetperiod));
			//TODO possible lookout place
			//	System.out.println("offeredrate"+offeredrate);
			//System.out.println("profitmult"+profitmulitplier);
			//	System.out.println("deducprob"+deducprob);
			//System.out.println("expected numvis cond"+expectedvalgivenabovededuc);
			if(numrejections==0)
			{
				offeredrate=(profitmulitplier+(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(numrejections==1)
			{
				offeredrate=(profitmulitplier)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(numrejections==2&&needmorepatients)
			{
				offeredrate=(profitmulitplier-(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			else if(numrejections==3&&needmorepatients)
			{
				offeredrate=(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(mean==0)
			{
				offeredrate=rate;
			}
			if(offeredrate>gov.getmaxrateins(insurernum))
			{
				offeredrate=gov.getmaxrateins(insurernum);
				feasibleoffer=false;
			}

		}
		if(!feasibleoffer)
		{
			offeredrate=Double.MAX_VALUE;
		}
		//System.out.println("real offeredrate"+offeredrate);
		//		try {
		//			Thread.sleep(20000000);
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
		return offeredrate;
	}
	public void offeraccepted(int pat, double offer)
	{
		patientrates.put(pat, offer);
		deductiblespaid.put(pat, (double) 0);
		numvisitsperpatient.put(pat, 0);
		numperiodsinf.put(pat, 0);
		if(!numvisitsperpatientold.containsKey(pat))
		{
			numvisitsperpatientold.put(pat, 0);
			numperiodsinfold.put(pat, 0);
		}

	}
	private double getpatET(int id) {
		double estET=baseET;
		//boolean alreadycalced=false;
		if(numvisitsperpatient.containsKey(id))
		{
			double numvisits=numvisitsperpatient.get(id);
			double numpinf=numperiodsinf.get(id);
			double currentET=numvisits/numpinf;
			boolean disregardcurrinf=false;
			if(numpinf!=0)
			{
				estET=currentET;
			}
			if(Double.isNaN(estET))
			{
				System.out.println("problem in new inf");
			}
			else
			{
				disregardcurrinf=true;
			}
			if(numperiodsinf.get(id)<pertoirrel)
			{

			}
			else
			{
				if(numvisitsperpatientold.containsKey(id))
				{
					if(!disregardcurrinf) 
					{
					estET=estET/(1+oldinfomult);
					estET=estET+oldinfomult*numvisitsperpatientold.get(id)/numperiodsinfold.get(id);
					}
					else
					{
						estET=numvisitsperpatientold.get(id)/numperiodsinfold.get(id);
					}
					if(Double.isNaN(estET))
					{
						System.out.println("problem in old inf");
					}
				}
			}
		}

		return estET;
	}

	public boolean patientstillworth(int p)
	{
		double ET=this.getpatET(p);

		boolean worth=false;
		if(ET!=0)
		{
			double mean=deductibleresetperiod*ET;
			double std=Math.pow(deductibleresetperiod*ET*(1-ET),0.5);
			NormalDistribution normdist=new NormalDistribution(mean,std);
			double averagecosts=this.getpatcost(p);
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double frac=deductible/averagecosts;
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			if(profitmulitplier*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=patientrates.get(p)||(needmorepatients&&(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=patientrates.get(p)))
			{
				worth=true;
			}
		}
		else 
		{
			worth=true;
		}
		return worth;
	}
	private double getpatcost(int p) {
		double cost=0;
		if(lasthospitalperpat.containsKey(p))
		{
			cost=costs.get(lasthospitalperpat.get(p));
		}
		else
		{
			cost=this.getavgcosts();
		}
		return cost;
	}

	public double hospitaloffer(Hospital hos, int numrejec, boolean largeprovider)
	{

		double stdvisitsperpats=hos.getpatstddev();
		//System.out.println(stdvisitsperpats);
		double meanvisitsperpats=hos.getmeanpats(deductibleresetperiod);
		//System.out.println(meanvisitsperpats);
		//	System.out.println(hos.patienthistory);
		if(stdvisitsperpats<0.001)
		{
			stdvisitsperpats=0.001;
		}
		NormalDistribution normdist=new NormalDistribution(meanvisitsperpats, stdvisitsperpats);
		NormalDistribution altnormdist=new NormalDistribution(meanvisitsperpats-stdvisitsperpats, stdvisitsperpats);



		double getnoinsprice=hos.getprice();
		boolean accept=false;
		double offer=getnoinsprice-0.01;
		//Very inefficient but correct code
		while(!accept&&offer>0)
		{
			double deducprob=1-normdist.cumulativeProbability(deductible/offer);

			//System.out.println("deducprob"+deducprob);
			double expectedvalgivenabovededuc=meanvisitsperpats+stdvisitsperpats*(normdist.density(deductible/offer))/(1-normdist.cumulativeProbability(deductible/offer));
			double frac=deductible/offer;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
//			System.out.println("offer multiplier  level is "+profitmulitplier*(deducprob*coverrate*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod)));
//			System.out.println("deduc multiplier  level is "+deducprob);
//			System.out.println("mean visits per pats is "+meanvisitsperpats);
//			System.out.println("expectedval multiplier  level is "+expectedvalgivenabovededuc);
//								try {
//									Thread.sleep(20000000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
			if(numrejec==0)
			{
				if((profitmulitplier+(profitmulitplier-1)*0.5)*(deducprob*coverrate*offer*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate)
				{
					accept=true;
				}
				//				System.out.println("totaloffer"+(profitmulitplier+(profitmulitplier-1)*0.5)*(deducprob*coverrate*offer*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod)));
				//				System.out.println("profit mult only"+(profitmulitplier+(profitmulitplier-1)*0.5));
				//				System.out.println("core costs only"+stdvisitsperpats);
				//				try {
				//					Thread.sleep(200000);
				//				} catch (InterruptedException e) {
				//					// TODO Auto-generated catch block
				//					e.printStackTrace();
				//				}
			}
			else if(numrejec==1)
			{
				if(profitmulitplier*(deducprob*coverrate*offer*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate)
				{
					accept=true;
				}
			}
			else if(numrejec>1&&!needmorepatients)
			{
				double altdeducprob=1-altnormdist.cumulativeProbability(deductible/offer);
				double altexpectedvalgivenabovededuc=meanvisitsperpats-stdvisitsperpats+stdvisitsperpats*(altnormdist.density(deductible/offer))/(1-altnormdist.cumulativeProbability(deductible/offer));

				altexpectedvalgivenabovededuc=altexpectedvalgivenabovededuc-frac;
				if(profitmulitplier*(altdeducprob*coverrate*offer*altexpectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate&&largeprovider)
				{
					accept =true;
				}

			}
			else if(numrejec>1&&needmorepatients)
			{
				if((profitmulitplier-(profitmulitplier-1)*0.5)*(deducprob*coverrate*offer*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate)
				{
					accept=true;
				}
				double altdeducprob=1-altnormdist.cumulativeProbability(deductible/offer);
				double altexpectedvalgivenabovededuc=meanvisitsperpats-stdvisitsperpats+stdvisitsperpats*(altnormdist.density(deductible/offer))/(1-altnormdist.cumulativeProbability(deductible/offer));
				altexpectedvalgivenabovededuc=altexpectedvalgivenabovededuc-frac;
				if(profitmulitplier*(altdeducprob*coverrate*offer*altexpectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate&&largeprovider)
				{
					accept =true;
				}

			}
			if(!accept)
			{
				offer=offer-0.1;
			}
		}
		if(!accept)
		{
			offer=-Double.MAX_VALUE;
		}
		return offer;
	}
	public double renegotiate(int hos)
	{
		LinkedHashSet<Integer> patsgoingtothishos=new LinkedHashSet<Integer>();
		for(int i:lasthospitalperpat.keySet())
		{
			if(lasthospitalperpat.get(i)==hos)
			{
				patsgoingtothishos.add(i);
			}
		}
		double totalcosts=0;
		double totalincome=0;
		for(int i:patsgoingtothishos)
		{
			double ET=this.getpatET(i);
			double mean=ET*deductibleresetperiod;
			double std=Math.pow(mean*(1-ET),0.5);
			NormalDistribution normdist=new NormalDistribution(mean,std);
			double averagecosts=costs.get(hos);
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			double frac=deductible/averagecosts;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;

			totalcosts=totalcosts+(deducprob*coverrate*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));
			totalincome=((double)patientrates.get(i));
		}
		double frac=totalincome/(profitmulitplier*totalcosts);// fraction of actual income to wanted income
		double offer=0;

		if(frac>1||totalcosts==0)
		{
			offer=costs.get(hos);
		}
		else
		{
			offer=costs.get(hos)*(1/frac);
		}
		if(Double.isNaN(offer))
		{
			System.out.println(" offer is NaN totalcosts is"+totalcosts);
		}
		return offer;
	} 
	public double patrenegotiate(Patient p, int numrejections)
	{
		double offeredrate=-10;
		double locET=0;
		boolean feasibleoffer=true;
		int ID=p.getID();
		if(!gov.providesinfoonpattoins(insurernum,p.getID()))
		{
			locET=this.getpatET(p.getID());
		}
		else 
		{
			locET=gov.getpatientETinfospec(ID);
		}
		double mean=deductibleresetperiod*locET;
		if(mean==0)
		{
			mean=0.1;
			locET=0.1/deductibleresetperiod;
		}
		double std=Math.pow(deductibleresetperiod*locET*(1-locET),0.5);
		if(std==0)
		{
			std=0.0001;
		}
		NormalDistribution normdist=new NormalDistribution(mean,std);

		if(fixedrate&&gov.obligationinsurance(insurernum))
		{
			offeredrate=rate;
			if(offeredrate>gov.getmaxrateins(insurernum))
			{ 
				offeredrate=gov.getmaxrateins(insurernum);
			}
		}
		else if(fixedrate)
		{
			double averagecosts=this.getpatcost(p.getID());
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			double frac=deductible/averagecosts;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			if(profitmulitplier*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate||(needmorepatients&&(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod))<=rate))
			{
				offeredrate=rate;

			}
			else
			{
				feasibleoffer=false;
			}
			if(offeredrate>gov.getmaxrateins(insurernum)&&gov.obligationinsurance(insurernum))
			{

				feasibleoffer=false;
			}
		}
		else if (gov.obligationinsurance(insurernum))
		{
			double averagecosts=this.getpatcost(p.getID());
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			double frac=deductible/averagecosts;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			offeredrate=profitmulitplier*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));
			if(numrejections==0)
			{
				offeredrate=(profitmulitplier+(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(numrejections==1&&needmorepatients)
			{
				offeredrate=(profitmulitplier-(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			else if(numrejections==2&&needmorepatients)
			{
				offeredrate=(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(offeredrate>gov.getmaxrateins(insurernum)&&gov.obligationinsurance(insurernum))
			{
				offeredrate=gov.getmaxrateins(insurernum);
				feasibleoffer=true;
			}
		}
		else
		{
			double averagecosts=this.getpatcost(p.getID());
			double deducprob=1-normdist.cumulativeProbability(deductible/averagecosts);
			double expectedvalgivenabovededuc=mean+std*(normdist.density(deductible/averagecosts))/(1-normdist.cumulativeProbability(deductible/averagecosts));
			double frac=deductible/averagecosts;
			expectedvalgivenabovededuc=expectedvalgivenabovededuc-frac;
			offeredrate=profitmulitplier*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));
			if(numrejections==0)
			{
				offeredrate=(profitmulitplier+(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(numrejections==1&&needmorepatients)
			{
				offeredrate=(profitmulitplier-(profitmulitplier-1)*0.5)*(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			else if(numrejections==2&&needmorepatients)
			{
				offeredrate=(deducprob*coverrate*averagecosts*expectedvalgivenabovededuc*((double)ratepayperiod  /deductibleresetperiod));

			}
			if(offeredrate>gov.getmaxrateins(insurernum)&&gov.obligationinsurance(insurernum))
			{
				offeredrate=gov.getmaxrateins(insurernum);
				feasibleoffer=false;
			}
		}
		if(!feasibleoffer)
		{
			offeredrate=Double.MAX_VALUE;
		}
		return offeredrate;
	}
	private double getavgcosts() {
		double avgcosts=0;
		if(costs.size()>0)
		{
			for(Map.Entry<Integer,Double> hospitalcosts : costs.entrySet())
			{
				avgcosts=avgcosts+hospitalcosts.getValue();
			}
			avgcosts=avgcosts/costs.entrySet().size();
		}
		else
		{
			avgcosts=(profitmulitplier*(rate+deductible))/(baseET*deductibleresetperiod)-0.01;
		}
		return avgcosts;
	}
	public LinkedHashMap<Integer,Double> getcosts()
	{
		return costs;
	}
	public void patientaccepted(Patient patient, double offer) {
		int IDP=patient.getID();
		patients.add(IDP);
		if(!numperiodsinf.containsKey(IDP))
		{
			numperiodsinf.put(IDP,0);
			numvisitsperpatient.put(IDP, 0);
		}

		patientrates.put(IDP, offer);
		deductiblespaid.put(IDP,(double) 0);
	}

	public void addhospital(int hospital, double compensation) {
		hospitals.add(hospital);
		costs.put(hospital,compensation);
		prices.put(hospital, (1-coverrate)*compensation);

	}
	public void removehospital(int hospital)
	{
		hospitals.remove(hospital);
		costs.remove(hospital);
		prices.remove(hospital);
	}
	public void determineiffixed(boolean fixed)
	{
		fixedrate=fixed;
	}
	public void updateaftervisit(int p,int hos)
	{
		if(!numvisitsperpatient.containsKey(p))
		{
			System.out.println("p"+p);
			System.out.println("patients"+patients);
		}
		int numvists=numvisitsperpatient.get(p);
		numvisitsperpatient.put(p,numvists+1);
		
		if(costs.containsKey(hos))
		{
			//System.out.println("ins profit before"+profit+"hos"+hos+"hospitals"+costs);

			if(deductible-deductiblespaid.get(p)<costs.get(hos))
			{
				profit=profit-(costs.get(hos)-(deductible-deductiblespaid.get(p)))*coverrate;//TODO
				if(deductible-deductiblespaid.get(p)>0)
				{
					deductiblespaid.put(p,deductible);
				}
				//System.out.println("first"+(deductible-deductiblespaid.get(p)));
			}
			else
			{
				deductiblespaid.put(p, deductiblespaid.get(p)+deductible-deductiblespaid.get(p));
				//System.out.println("second");

			}
			//System.out.println("ins profit after"+profit);
//			try {
//				Thread.sleep(20000000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
	public void updateprofiteveryperiod()
	{
		ratepaytimer--;
		for(Entry<Integer,Double> p:patientrates.entrySet())
		{
			if(ratepaytimer==1)
			{
				profit=profit+p.getValue();
			}
			//System.out.println(this.getpatients());

		}
		if(ratepaytimer==0)
		{
			ratepaytimer=ratepayperiod;
		}
		profithistory.add(profit);
		profit=0;
		double totaltimeaverage=0;
		double totalprofit=0;
		for(Entry<Integer,Double> p:patientrates.entrySet())
		{
		numperiodsinf.put(p.getKey(),numperiodsinf.get(p.getKey())+1);
		}
		for(int i=0;i<deductibleresetperiod;i++)
		{
			if(profithistory.size()>i)
			{
				totalprofit=totalprofit+profithistory.get(profithistory.size()-1-i);
				totaltimeaverage++;
			}
		}
		if(goalprofit>totalprofit/totaltimeaverage)
		{
			needmorepatients=true;
		}
		else
		{
			needmorepatients=false;
		}
		deductibleresettimer--;
		if(deductibleresettimer<=0)
		{
			deductibleresettimer=deductibleresetperiod;
			for(int pat:deductiblespaid.keySet())
			{
				deductiblespaid.put(pat, (double) 0);
			}
		}
		time++;
	}

	public double determinecopayment(int hospital, double compensation) {
		double copayment=compensation*(1/coverrate-1);
		return copayment;

	}
	public boolean checkifpatstillsameET(int pat)
	{
		double currentestET=this.getpatET(pat);
		boolean sameet=true;
		if(patientvisits.containsKey(pat))
		{
			int it=patientvisits.get(pat).size()-1;
			boolean beforecutoff=true;
			double numvisits=0;
			while(it>=0&&beforecutoff)
			{
				if(patientvisits.get(pat).get(it)>time-180)
				{
					numvisits++;
				}
				it=it-1;
			}
			double divisor=180;
			if(time<180)
			{
				divisor=time;
			}
			double currstd=Math.pow(currentestET*divisor*(1-currentestET),0.5);
			NormalDistribution normdist=new NormalDistribution(currentestET*divisor,currstd);
			if(numvisits/divisor>currentestET)
				if(1-normdist.cumulativeProbability(numvisits/divisor)<0.1)
				{
					sameet=false;
				}
				else
				{
					if(normdist.cumulativeProbability(numvisits/divisor)<0.1)
					{
						sameet=false;
					}
				}
			if(!sameet)
			{
				numvisitsperpatientold.put(pat,numvisitsperpatientold.get(pat)+numvisitsperpatient.get(pat));
				numvisitsperpatient.put(pat, 0);
				numperiodsinfold.put(pat, numperiodsinfold.get(pat)+numperiodsinf.get(pat));
				numperiodsinf.put(pat, 0);
			}
		}
		return sameet;
	}
	public double getdeductible()
	{
		return deductible;
	}
	public void setETtomean()
	{
		double total=0;
		double numpats=0;
		for(int p:numvisitsperpatient.keySet())
		{
			if(numperiodsinf.get(p)>0)
			{
				total=total+(double)numvisitsperpatient.get(p)/((double)numperiodsinf.get(p));
				numpats++;
			}
		}
		if(numpats>0)
		{
		baseET=total/numpats;
		}
	}
	public void setbaseratetomean()
	{
		double meanrate=0;
		if(patients.size()>0)
		{
		for(int i:patients)
		{
			meanrate=meanrate+patientrates.get(i);
		}
		rate=meanrate/patients.size();
		}
	}
	public void setbaseEt(double newET)
	{
		baseET=newET;
	}
	public double getprofit()
	{
		return profit;
	}
	public ArrayList<Double> getprofithistory()
	{
		return profithistory;
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
	public void removepatient(Patient patient) {
		int IDP=patient.getID();
		patients.remove(IDP);
		if(!numperiodsinf.containsKey(IDP))
		{
			if(numvisitsperpatientold.containsKey(IDP))
			{
				numvisitsperpatientold.put(IDP,numvisitsperpatientold.get(IDP)+numvisitsperpatient.get(IDP));
				numperiodsinfold.put(IDP,numperiodsinfold.get(IDP)+numperiodsinf.get(IDP));
			}
			else
			{
				numvisitsperpatientold.put(IDP,numvisitsperpatient.get(IDP));
				numperiodsinfold.put(IDP,numperiodsinf.get(IDP));
			}
			numperiodsinf.remove(IDP);
			numvisitsperpatient.remove(IDP);
		}

		patientrates.remove(IDP);
		deductiblespaid.remove(IDP);
	}
}

