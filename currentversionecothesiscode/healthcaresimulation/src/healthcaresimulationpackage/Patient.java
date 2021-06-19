package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;
public class Patient extends Localizedagent implements Deepcopiableactor {

	private double budget;
	private Patientobjectivefactor objfac;
	int patientnum;
	private ArrayList<Integer> restrictioninfluence;
	private LinkedHashSet<Integer> hospitalset;
	private double chanceofhospital; //the chance of needing a hospital per period
	//private double ET; //expected time until a hospital visit is needed equal to E(T
	// also expected number of timeperiod between visits including the eventual visit 
	private int insurernum;
	public double insurerrate;// amount paid to hospital per period
	private double penalty;// penalty if denied entry by hospital for quality of hospital
	private double  currentmoney;
	private int numhospitalvisits;//number of hospitals visits this patient has had
	private int unsucnumhospitalvisits;// number
	private int tooexpensivenumhospitalvisits;//number of hospital visits that made the 
	private int numperiodsinbudget;//the number of periods before the patient gets paid again
	private int numbudgetperiodstotal;//number of periods in general in the budget
	private double riskseeking;//how riskseeking a patient is 1 is riskneutral the higher the more riskseeking the lower the more conservative
	private double hospitalchoicetremblinghandchance;// chance that a random different hospital from  the chosen hospital is picked
	private double nohospitaloverwrite;//chance that when no hospital is chosen a random hospital is chosen
	private Randomdraws tremblehand;// Random variable that determines if a patient goes to a hospital he does not prefer
	//TODO fix deductible and the following functions: updatepatientaftervisit 
	//, acceptinsureroffer,checkifinsurancestillworthit also deepcopy related and intialization
	//also the objective function needs to be changed aswell as possible other functions.
	private double deductibleamount;//out of pocket deductible that at the bgein of the period has to be paid
	private double deductiblepaid;//how much of the deductible is already paid
	private int deductibleresetperiod;// when the deductible resets
	private int deductibletime;//How much time it still takes to 
	private boolean rej;
	private int numrejec;//number of subsequent rejections
	//public static int disillusioned;//TODO remove this later
	//public static int totalvisits;//TODO remove this later
	//public static int hos1vis;//TODO remove this later
	public Patient(Location loc, double inbudget, 
			Patientobjectivefactor initobjfac,
			int patientnumber, Government gov,
			LinkedHashSet<Integer> inhospitalset,
			double inchanceofhospital, double inpenalty,
			int innumperiodsinbudget,
			double inriskseeking,
			double inhospitalchoicetremblinghandchance,
			double innohospitaloverwrite,
			Randomdraws intremblehand,int innumbudgetperiodstotal) {
		super(loc,gov);
		budget=inbudget;
		objfac=initobjfac;
		patientnum=patientnumber;
		//		if(objfac.multipliers.get(0)==3.2900256329914495)
		//		{
		//			//System.out.println("this is the patient num: "+patientnum);
		//		}
		hospitalset=inhospitalset;
		restrictioninfluence=new ArrayList<Integer>();
		for(int i=0;i<5;i++)
		{
			restrictioninfluence.add(0);
		}
		chanceofhospital=inchanceofhospital;
		//ET=1/chanceofhospital;
		insurernum=-1;
		penalty=inpenalty;
		insurerrate=0;
		currentmoney=budget;
		numhospitalvisits=0;
		unsucnumhospitalvisits=0;
		tooexpensivenumhospitalvisits=0;
		numperiodsinbudget=innumperiodsinbudget;
		riskseeking=inriskseeking;
		hospitalchoicetremblinghandchance=inhospitalchoicetremblinghandchance;
		nohospitaloverwrite=innohospitaloverwrite;
		tremblehand=intremblehand;
		this.updateobjbudget(budget);
		deductiblepaid=0;
		deductibletime=0;
		rej=false;
		numrejec=0;
		//disillusioned=0;
		//totalvisits=0;
		//hos1vis=0;
		numbudgetperiodstotal=innumbudgetperiodstotal;
		deductibleresetperiod=innumbudgetperiodstotal;
	}
	public Patient(Location loc, double inbudget, 
			Patientobjectivefactor initobjfac,
			int patientnumber, Government gov,
			LinkedHashSet<Integer> inhospitalset,
			double inchanceofhospital, double inpenalty, int ininsurernum, double ininsurerrate
			,ArrayList<Integer> inrestrictioninfluence, double incurrentmoney, int innumhospitalvisits,
			int inunsucnumhospitalvisits, int intooexpensivenumhospitalvisits,int innumperiodsinbudget,
			double inriskseeking, double inhospitalchoicetremblinghandchance,
			double innohospitaloverwrite,
			Randomdraws intremblehand, boolean rejin, int innumrejec, int innumbudgetperiodstotal)
	{
		super(loc,gov);
		budget=inbudget;
		objfac=initobjfac;
		patientnum=patientnumber;
		hospitalset=inhospitalset;
		restrictioninfluence=inrestrictioninfluence;
		chanceofhospital=inchanceofhospital;
		//ET=1/chanceofhospital;
		insurernum=ininsurernum;
		insurerrate=ininsurerrate;
		penalty=inpenalty;
		currentmoney=incurrentmoney;
		numhospitalvisits=innumhospitalvisits;
		unsucnumhospitalvisits=inunsucnumhospitalvisits;
		tooexpensivenumhospitalvisits=intooexpensivenumhospitalvisits;
		numperiodsinbudget=innumperiodsinbudget;
		riskseeking=inriskseeking;
		hospitalchoicetremblinghandchance=inhospitalchoicetremblinghandchance;
		nohospitaloverwrite=innohospitaloverwrite;
		tremblehand=intremblehand;
		this.updateobjbudget(currentmoney);
		rej=rejin;
		numrejec=innumrejec;
		numbudgetperiodstotal=innumbudgetperiodstotal;
	}
	/**
	 * this function updates the objective function of the patient, the information is coming from the governement this can be called for all hospitals with the averages
	 * @param allowedtoenter
	 * @param performance
	 * @param cost
	 * @param hospital
	 */
	public void updatepatientaftervisit(boolean allowedtoenter,double performance, double cost, int hospital,boolean externalinformation )
	{
		if(performance>8)
		{
			//System.out.println("something is wrong");
		}
		if(allowedtoenter) 
		{
			//TODO might be wrong.
			objfac.update(performance,cost, hospital,externalinformation,false);//Math.pow(2,numrejec+1)*
			if(currentmoney<cost&&!externalinformation)
			{
				tooexpensivenumhospitalvisits = getTooexpensivenumhospitalvisits() + 1;

			}
			if(!externalinformation)
			{
				numrejec=0;
				objfac.updaterejec(numrejec);
				rej=false;
				if(!objfac.isinsured(hospital))
				{
					currentmoney=currentmoney-cost;
				}
				else if(deductiblepaid>=deductibleamount)
				{
					currentmoney=currentmoney-objfac.getprices().get(hospital);
				}
				else
				{
					//System.out.println("pat currmon before"+currentmoney+"hos id"+hospital+"ins"+insurernum);
					if(deductibleamount-deductiblepaid-objfac.getpredeductibleprice(hospital)>0)
					{
						currentmoney=currentmoney-objfac.getpredeductibleprice(hospital);
						deductiblepaid=deductiblepaid+objfac.getpredeductibleprice(hospital);
					}
					else 
					{
						double topayafterdeduc=objfac.getpredeductibleprice(hospital)-deductibleamount+deductiblepaid;
						currentmoney=currentmoney-deductibleamount+deductiblepaid-
								(objfac.getprices().get(hospital)/objfac.getpredeductibleprice(hospital))*topayafterdeduc;
						deductiblepaid=deductibleamount;
					}
					//System.out.println("pat currmon after"+currentmoney);
				}
				this.updateobjbudget(currentmoney);

			}
		}
		else
		{
			objfac.update(-penalty*Math.pow(2,numrejec+1),cost, hospital,externalinformation,true);//adjust
			if(!externalinformation)
			{
				unsucnumhospitalvisits = getUnsucnumhospitalvisits() + 1;
				rej=true;
				if(numrejec<181)
				{
					numrejec=numrejec+1;
				}
				objfac.updaterejec(numrejec);

			}
		}
		if(!externalinformation)
		{
			numhospitalvisits = getNumhospitalvisits() + 1;


			// to see relation between overal budget and budget per time period
		}
	}
	/**
	 * get the best hospital that the patient can go to, if the patient with his current budget cannot go to any hospital in his view the function returns -1
	 * @return
	 */
	public int getbesthospital()
	{
		//		LinkedHashMap<Integer,Double> possibleobjec=objfac.getscores();
		//		int besthospital=-1;
		//		double locmax=-Double.MAX_VALUE;
		//		for(Map.Entry<Integer,Double> hospital : possibleobjec.entrySet())
		//		{
		//			if(hospital.getValue()>locmax)
		//			{
		//				locmax=hospital.getValue();
		//				besthospital=hospital.getKey();
		//			}
		//		}
		//		if(besthospital<0)
		//		{
		//			Loggers.messagelogger("Patient "+this.patientnum +" could not find a feasible hospital and has defaulted to hospital 0");
		//			besthospital=0;
		//		}
		int besthospital=objfac.choosebestoption();
		//totalvisits++;
		boolean chancedhospitalchoice=false;
		if(besthospital<0)
		{
			//disillusioned++;
			//System.out.println(objfac.getscores());
			if(tremblehand.draw()<nohospitaloverwrite)
			{
				chancedhospitalchoice=true;
			}
		}
		else
		{
			if(tremblehand.draw()< hospitalchoicetremblinghandchance)
			{
				chancedhospitalchoice=true;
			}
		}
		if(chancedhospitalchoice)
		{
			besthospital=tremblehand.randomselection(hospitalset);
			//hos1vis=hos1vis+besthospital;

		}
		return besthospital;
	}
	/**
	 * 
	 * @return
	 */
	public ArrayList<Double> getbesthospitalandvalue()
	{
		ArrayList<Double> besthosval= new ArrayList<Double>();
		int hospital=objfac.choosebestoption();
		double value=objfac.evaluatefunctionperelement(hospital);
		//System.out.println("1 has a value of"+objfac.evaluatefunctionperelement(1));
		besthosval.add((double) hospital);
		besthosval.add(value);
		return besthosval;
	}
	/**
	 * 
	 * @param insurer
	 * @param price is the price per period the patient pays
	 * @return
	 */
	// check if conditions work also make second one to check if still worth it.
	public boolean acceptinsureroffer(Insurer insurer, double rate, double deductibleoffer)
	{
		boolean accept=false;
		//there are two conditions that determine if the patient accepts an insurers offer.
		Patientobjectivefactor objfaccopy=objfac.deepcopy();
		double bestval=-Double.MAX_VALUE;
		if(insurernum<=-1)
		{
			int besthospital=objfaccopy.choosebestoption();
			if(besthospital!=-1)
			{
				bestval=objfaccopy.evaluatefunctionperelement(besthospital);
				if(insurernum>-1)//TODO possibly have to add in text
				{
					bestval=bestval-insurerrate*objfaccopy.getthirdmult();
				}
				//System.out.println("besthospital old, is real best "+besthospital);
			}
			else
			{
				bestval=objfaccopy.evaluatefunctionperelement(objfaccopy.besthosifnovisit());
				//System.out.println("besthospital old, is not real best "+objfaccopy.besthosifnovisit());

			}
		}
		else
		{
			bestval=this.altinsurervalcalc(insurerrate, deductibleamount,
					objfaccopy.getinsuredhospitals(), 
					objfaccopy.getsetprices(), objfaccopy.getdeductiblesetprices());
		}
		// remove the following line; it should not be there
		//rate=0.1;
		//System.out.println("insured hospitals"+insurer.gethospitals());
		//System.out.println("pre deduc prices"+insurer.getcosts());
		//System.out.println("post deduc prices"+insurer.getprices());
		double altbestval=this.altinsurervalcalc(rate, deductibleoffer, insurer.gethospitals(), insurer.getprices(), insurer.getcosts());

		//			System.out.println("bestval "+bestval);
		//			System.out.println("altbestval"+altbestval);
		//				if(insurer.gethospitals().size()>0)
		//				{
		//					//.calcscores();
		//					System.out.println("line");
		//					objfaccopy.calcscores();
		//					try {
		//						Thread.sleep(20000000);
		//					} catch (InterruptedException e) {
		//						e.printStackTrace();
		//					}
		//		}
		if(bestval<altbestval)
		{
			accept=true;
		}
		//		}
		if(accept||(gov.obligatedinsurance(patientnum)&&insurernum<0))
		{
			if(insurernum!=insurer.getID())
			{
				deductibleamount=deductibleoffer;
				deductiblepaid=0;
			}
			insurernum=insurer.getID();
			insurerrate=rate;
			objfac.setinsurance(insurer.gethospitals(), insurer.getprices(),deductibleoffer, insurer.getcosts());
			objfac.resetdeductible();
			accept=true;
		}
		//		if(!accept&&insurer.gethospitals().size()>0)
		//		{
		//			//non active prints and sleep
		//			System.out.println("insured hospitals"+insurer.gethospitals());
		//			System.out.println("rate that is declined"+rate);
		//			System.out.println("amount of money this patient has"+currentmoney);
		//			System.out.println("pre deduc prices"+insurer.getcosts());
		//			System.out.println("current insurer"+this.getinsurer());
		//			System.out.println("bestval"+bestval);
		//			System.out.println("altbestval"+altbestval);
		//						try {
		//							Thread.sleep(20000000);
		//						} catch (InterruptedException e) {
		//							e.printStackTrace();
		//						}
		//
		//		}

		return accept;
	} 
	// include new accounting for rates and deductibles
	public boolean checkifinsurancestillworthit()
	{
		boolean worthit=true;
		if(insurernum<0||gov.obligatedinsurance(patientnum))
		{

		}
		else
		{
			//there are two conditions that determine if the patient accepts an insurers offer.
			//			objfac.calcscores();
			//			int besthospital=objfac.choosebestoption();
			//			if(besthospital==-1)
			//			{
			//				besthospital=objfac.besthosifnovisit();
			//			}
			LinkedHashSet<Integer> hospitals=new LinkedHashSet<Integer>();
			LinkedHashMap<Integer, Double> prices=new LinkedHashMap<Integer, Double>();
			double deducamount=0;
			LinkedHashMap<Integer,Double> deducpricesin=new LinkedHashMap<Integer,Double>();
			//			double bestval=objfac.evaluatefunctionperelement(besthospital);
			double bestval=this.altinsurervalcalc(insurerrate, deductibleamount,
					objfac.getinsuredhospitals(), 
					objfac.getsetprices(), objfac.getdeductiblesetprices());
			Patientobjectivefactor altobjfac=objfac.deepcopy();
			altobjfac.setinsurance(hospitals, prices, deducamount, deducpricesin);
			altobjfac.setbudget(riskseeking*budget/(numperiodsinbudget*chanceofhospital));
			int altbesthospital=altobjfac.choosebestoption();
			if(altbesthospital==-1)
			{
				altbesthospital=altobjfac.besthosifnovisit();
			}
			double altbestval=objfac.evaluatefunctionperelement(altbesthospital);
			if(bestval<altbestval)
			{
				worthit=false;
			}

		}
		return worthit;
	}
	public void setnullinsurer()
	{
		insurerrate=0;
		insurernum=-1;
		deductibleamount=0;
		deductiblepaid=0;
		deductibleresetperiod=0;
		deductibletime=0;
		LinkedHashSet<Integer> hospitals=new LinkedHashSet<Integer>();
		LinkedHashMap<Integer, Double> prices=new LinkedHashMap<Integer, Double>();
		double deducamount=0;
		LinkedHashMap<Integer,Double> deducpricesin=new LinkedHashMap<Integer,Double>();
		objfac.setinsurance(hospitals, prices, deducamount, deducpricesin);

	}
	public void setpricesandinsuredhospitals(Insurer insurer, double rate,
			double deducamount)
	{
		LinkedHashSet<Integer> hospitals=insurer.gethospitals();
		LinkedHashMap<Integer, Double> prices=insurer.getprices();
		objfac.setinsurance(hospitals, prices, deducamount, insurer.getcosts());
		insurernum=insurer.getID();
		insurerrate=rate;
		this.updateobjbudget(currentmoney);

	}
	public double getcurrentmoney()
	{
		return currentmoney;
	}
	/**
	 * governement 
	 * @return
	 */
	public void processgovernementoffer()
	{
		if(gov.isinformedpat(patientnum))
		{

			for (Integer hospital : hospitalset)
			{
				if(gov.hashospitalinfo(hospital))
				{
					boolean penaltybool=false;
					ArrayList<Double> hosinfo=gov.gethospitalinfo(hospital);
					if(hosinfo.get(1)>budget)
					{
						penaltybool=true;
					}
					objfac.update(hosinfo.get(0), hosinfo.get(1), hospital, true, penaltybool);
				}
			}
		}
	}
	public boolean needshospital(Random inrand )
	{
		double draw=inrand.nextDouble();
		boolean needshosptial=false;
		if(draw<=chanceofhospital||rej)
		{
			needshosptial=true;
		}
		return needshosptial;
	}
	/**
	 * note that the restrictions that do not directly affect the patients are not counted here.
	 * it should also be noted that this is intra model and is not truely accurate.
	 * it is used to measure the expected intervention influence as seen in this world
	 * this can be used for policy making as measuring perceived invasiness
	 */
	@Override
	public ArrayList<Integer> influenceduetorestrict()
	{
		return restrictioninfluence;
	}
	/**
	 * deepcopyagent does not copy the restrictioninfluence field
	 * 
	 */
	@Override
	public Patient deepcopyagent() {
		ArrayList<Integer> copyrestrictioninfluence=new ArrayList<Integer>();
		copyrestrictioninfluence.addAll(restrictioninfluence);
		LinkedHashSet<Integer> hospitalsetcopy=new LinkedHashSet<Integer>();
		for(int hospital:hospitalset) 
		{
			hospitalsetcopy.add(hospital);
		}
		/**
		 * Patient(Location loc, double inbudget, 
			Patientobjectivefactor initobjfac,
			int patientnumber, Government gov,
			LinkedHashSet<Integer> inhospitalset,
			double inchanceofhospital, double inpenalty, int ininsurernum, double ininsurerrate
			,ArrayList<Integer> inrestrictioninfluence, double incurrentmoney, int innumhospitalvisits,
			int inunsucnumhospitalvisits, int intooexpensivenumhospitalvisits,int innumperiodsinbudget,
			double inriskseeking)
		 */
		Patient patientcopy=new Patient(loc.deepcopy(), budget, objfac.deepcopy(), 
				patientnum, gov.deepcopyagent(),hospitalsetcopy,chanceofhospital, penalty, insurernum,
				insurerrate,copyrestrictioninfluence, currentmoney,numhospitalvisits,unsucnumhospitalvisits,
				tooexpensivenumhospitalvisits,numperiodsinbudget,riskseeking,
				hospitalchoicetremblinghandchance,
				nohospitaloverwrite,
				tremblehand.deepcopy(),rej,numrejec,numbudgetperiodstotal);
		return patientcopy;
	} 
	@Override
	public int getID()
	{
		return patientnum;
	}
	public int getinsurer()
	{
		return insurernum;
	}
	public void updatebudget(double budgetin)
	{
		budget=budgetin;
	}
	public void updateobjbudget(double currmon)
	{
		double insurfactor=0;
		double basefac=riskseeking*currmon/(numperiodsinbudget*chanceofhospital);
		if(insurernum>=0&&deductibleamount-deductiblepaid>0)
		{
			insurfactor=0;
			double expnumvis=numperiodsinbudget*chanceofhospital;
			int hos=objfac.choosebestoption();
			if(hos==-1)
			{
				hos=objfac.besthosifnovisit();
			}
			double prop=objfac.getpropprice(hos);
			double prededucprice=objfac.getpredeductibleprice(hos);
			double firstterm=(deductibleamount-deductiblepaid)/prededucprice;
			if(firstterm<0)
			{
				firstterm=0;
			}
			double expectednumdeducvisc=0;
			if((prededucprice*expnumvis)/riskseeking>deductibleamount-deductiblepaid)
			{
				expectednumdeducvisc=(prededucprice*expnumvis)/riskseeking-deductibleamount+deductiblepaid;
			}
			//needs to be changed
			double expectednumdeducvis=expectednumdeducvisc/prededucprice;
			double divisormod=expectednumdeducvis*(prop);
			if((prededucprice*expnumvis)/riskseeking>deductibleamount)
			{
				insurfactor=currmon/(firstterm+divisormod)-basefac;
			}

		} 
		// to see relation between overal budget and budget per time period
		objfac.setbudget(basefac+insurfactor);
	}

	public void updatecurrentmoneyperperiod()
	{
		numperiodsinbudget--;
		deductibletime--;


		if(numperiodsinbudget==0)
		{
			this.getpaid();
			currentmoney=currentmoney-insurerrate;

			numperiodsinbudget=numbudgetperiodstotal;
		}
		if(deductibletime==0)
			
		{
			this.resetdeductible();
			objfac.resetdeductible();
			deductibletime=deductibleresetperiod;
		} 
	}
	public void getpaid() {
		if(this.getID()==0)
		{
			//System.out.println("happens"+currentmoney+"budget"+ budget);
		}
		currentmoney=currentmoney+budget;
		double insurfactor=0;
		double basefac=riskseeking*currentmoney/(numperiodsinbudget*chanceofhospital);
		if(insurernum>=0&&deductibleamount-deductiblepaid>0)
		{
			insurfactor=0;
			double expnumvis=numperiodsinbudget*chanceofhospital;
			int hos=objfac.choosebestoption();
			double prop=objfac.getpropprice(hos);
			if(hos==-1)
			{
				hos=objfac.besthosifnovisit();
			}
			double prededucprice=objfac.getpredeductibleprice(hos);
			double firstterm=(deductibleamount-deductiblepaid)/prededucprice;
			if(firstterm<0 )
			{
				firstterm=0;
			}
			double expectednumdeducvisc=0;
			if((prededucprice*expnumvis)/riskseeking>deductibleamount-deductiblepaid)
			{
				expectednumdeducvisc=(prededucprice*expnumvis)/riskseeking-deductibleamount+deductiblepaid;
			}
			//needs to be changed
			double expectednumdeducvis=expectednumdeducvisc/prededucprice;
			double divisormod=expectednumdeducvis*(prop);
			if((prededucprice*expnumvis)/riskseeking>deductibleamount)
			{
				insurfactor=currentmoney/(firstterm+divisormod)-basefac;
			}

		} 
		objfac.setbudget(basefac+insurfactor);
	}
	/**
	 * deprecated
	 */
	public void resetcurrentmoney()
	{
		currentmoney=budget;
		double insurfactor=0;
		double basefac=riskseeking*currentmoney/(numperiodsinbudget*chanceofhospital);
		if(insurernum>=0&&deductibleamount-deductiblepaid>0)
		{
			insurfactor=0;
			double expnumvis=numperiodsinbudget*chanceofhospital;
			int hos=objfac.choosebestoption();
			double prop=objfac.getpropprice(hos);
			double prededucprice=objfac.getpredeductibleprice(hos);
			double firstterm=(deductibleamount-deductiblepaid)/prededucprice;
			if(firstterm<0 )
			{
				firstterm=0;
			}
			double expectednumdeducvisc=0;
			if((prededucprice*expnumvis)/riskseeking>deductibleamount-deductiblepaid)
			{
				expectednumdeducvisc=(prededucprice*expnumvis)/riskseeking-deductibleamount+deductiblepaid;
			}
			//needs to be changed
			double expectednumdeducvis=expectednumdeducvisc/prededucprice;
			double divisormod=expectednumdeducvis*(prop);
			if((prededucprice*expnumvis)/riskseeking>deductibleamount)
			{
				insurfactor=currentmoney/(firstterm+divisormod)-basefac;
			}

		} 
		objfac.setbudget(basefac+insurfactor);

	}
	public double altinsurervalcalc(double rate,double indeductibleamount,
			LinkedHashSet<Integer> insuredhospitalsin,
			LinkedHashMap<Integer,Double> setpricesin,
			LinkedHashMap<Integer,Double> deductiblesetpricesin)
	{
		double highestval=-Double.MAX_VALUE;
		double diff=-rate;// possibly correct or change in text was insurerrate-rate
		double currmon=currentmoney+diff;
		double basefac=riskseeking*currmon/(numperiodsinbudget*chanceofhospital);
		//System.out.println("basefac"+basefac+"currentmoney is "+currentmoney+" currmon "+currmon);
		for(int hos:hospitalset)
		{

			double insurfactor=0;
			Patientobjectivefactor altobjfac=objfac.deepcopy();
			double expectednumdeducvis=numperiodsinbudget*chanceofhospital/riskseeking;//possibly change
			double totalvisits=numperiodsinbudget*chanceofhospital/riskseeking;
			if(indeductibleamount>0)
			{
				altobjfac.setinsurance(insuredhospitalsin, setpricesin, indeductibleamount, deductiblesetpricesin);
				//				System.out.println("oldcostsest "+objfac.getestimatedcostsandprices());
				//				System.out.println("newcostsest "+altobjfac.getestimatedcostsandprices());
				//				System.out.println(altobjfac.getpredeductibleprice(hos));
				double expnumvis=numperiodsinbudget*chanceofhospital;
				double prop=altobjfac.getpropprice(hos);
				//System.out.println("prop"+prop);
				//	System.out.println("prop "+prop);
				double prededucprice=altobjfac.getpredeductibleprice(hos);
				double firstterm=(indeductibleamount)/prededucprice;
				if(firstterm<0)
				{
					firstterm=0;
				}
				//	System.out.println("firstterm"+firstterm);
				double expectednumdeducvisc=0;
				if((prededucprice*expnumvis)/riskseeking>indeductibleamount-0)
				{
					expectednumdeducvisc=(prededucprice*expnumvis)/riskseeking-indeductibleamount+0;
					//System.out.println("numvis check"); 
				}
				expectednumdeducvis=expectednumdeducvisc/prededucprice;
				double divisormod=expectednumdeducvis*(prop);
				if((prededucprice*expnumvis)/riskseeking>indeductibleamount)
				{
					insurfactor=currentmoney/(firstterm+divisormod)-basefac;
					//System.out.println("check"+insurfactor);
				}
				//double divisor=firstterm+divisormod;
				//	System.out.println("full divisor "+divisor+"basefac"+basefac);
			}
			altobjfac.setbudget(basefac+insurfactor);
			//			System.out.println("oldbudget "+objfac.getbudget());
			//			System.out.println("newbudget "+altobjfac.getbudget());
			//			objfac.calcscores();
			//			altobjfac.calcscores();
			//			try {
			//			Thread.sleep(200000);
			//		} catch (InterruptedException e) {
			//			Auto-generated catch block
			//			e.printStackTrace();
			//		}
			//System.out.println("best choice"+altobjfac.choosebestoption());
			int bestchoice=altobjfac.choosebestoption();
			//System.out.println("asssuming hospital"+hos+"best choice is"+bestchoice);
			if(setpricesin.containsKey(hos))
			{
				if(bestchoice!=-1)
				{
					double lochighval=altobjfac.evaluatefunctionperelement(bestchoice);
					if(indeductibleamount>setpricesin.get(hos))
					{
						lochighval=lochighval+objfac.getthirdmult()*(deductiblesetpricesin.get(hos)-setpricesin.get(hos))*expectednumdeducvis;
					}
					else
					{
						double partuninsured=totalvisits-expectednumdeducvis;
						lochighval=lochighval+objfac.getthirdmult()*partuninsured*(deductiblesetpricesin.get(hos)-setpricesin.get(hos))*expectednumdeducvis;

					}
					if(lochighval>highestval)
					{
						highestval=lochighval;
					}
					//System.out.println("true");

				}
				else
				{
					//System.out.println("altbest"+altobjfac.besthosifnovisit());
					double lochighval=altobjfac.evaluatefunctionperelement(altobjfac.besthosifnovisit());
					if(indeductibleamount>setpricesin.get(hos))
					{
						lochighval=lochighval+objfac.getthirdmult()*(deductiblesetpricesin.get(hos)-setpricesin.get(hos))*expectednumdeducvis;

					}
					else
					{
						double partuninsured=totalvisits-expectednumdeducvis;
						lochighval=lochighval+objfac.getthirdmult()*partuninsured*(deductiblesetpricesin.get(hos)-setpricesin.get(hos))*expectednumdeducvis;


					}
					if(lochighval>highestval)
					{
						highestval=lochighval;
					}
					//				if(this.getinsurer()!=-1)
					//				{
					//				System.out.println("best hospital is not -1 happens");
					//				System.out.println(this.getinsurer());
					//				try {
					//					Thread.sleep(200000);
					//				} catch (InterruptedException e) {
					//					//  Auto-generated catch block
					//					e.printStackTrace();
					//				}
					//				}
				}
			}

		}
		highestval=highestval-rate*objfac.getthirdmult();// possibly remove
		//System.out.println("new val"+highestval+"new factor"+rate*objfac.getthirdmult());
		return highestval;
	}
	public void resetdeductible()
	{
		deductibletime=deductibleresetperiod;
		deductiblepaid=0;
	}
	public int getNumhospitalvisits() {
		return numhospitalvisits;
	}
	public int getUnsucnumhospitalvisits() {
		return unsucnumhospitalvisits;
	}
	public int getTooexpensivenumhospitalvisits() {
		return tooexpensivenumhospitalvisits;
	}
	public double getspecrealcosts(int hos)
	{
		return objfac.getrealprice(hos);
	}
	public double getbudget()
	{
		return budget;
	}
	/**
	 * used to intiate time conception
	 * @param numperiodsstillinbudget
	 */
	public void setnumperiodsstillinbudget(int numperiodsstillinbudget)
	{
		numperiodsinbudget=numperiodsstillinbudget;
	}
	public double getchanceofhospital()
	{
		return chanceofhospital;
	}
	public void setchanceofhospital(double inchanceofhospital)
	{
		chanceofhospital=inchanceofhospital;
	}
}
