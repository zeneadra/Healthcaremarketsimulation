package healthcaresimulationpackage;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

public class Insurertest {

	public static void main(String[] args) {

		int seed=7545;
		double nocarepenalty=100;
		Random randgen=new Random(seed);
		Loggers.clearerrorlogger();//TODO remember to add in final executable
		//LinkedHashMap<Integer,Double> empty=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> inpricemaxes=new LinkedHashMap<Integer,Double>();
		LinkedHashSet<Integer> inpricerestrictedhospitals=new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> inobligatedinsurers=new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> inobligatedpatients=new LinkedHashSet<Integer>();
		double inmaxrate=Double.MAX_VALUE;
		LinkedHashSet<Integer> ininformedins=new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> intrackedpatients=new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> ininformedpats=new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> intrackedhospitals=new LinkedHashSet<Integer>();
		Government gov=new Government(inpricemaxes,inpricerestrictedhospitals,inobligatedinsurers,
				inobligatedpatients,inmaxrate,ininformedins,intrackedpatients,
				ininformedpats,intrackedhospitals);
		Randomdraws locdraw=new Randomdraws(0,7,"u",randgen,seed);

		Location hosloc1=new Location(2.5,3.5);
		Location hosloc2=new Location(3.5,2.5);
		double closedistancemeasure=3;
		Randomdraws initqualdist1=new Randomdraws(2,5,"u",randgen.nextInt());
		Randomdraws initqualdist2=new Randomdraws(2,5,"u",randgen.nextInt());
		int cap=500;
		double startprice=7;
		LinkedHashSet<Integer> allinsurers=new LinkedHashSet<Integer>();
		allinsurers.add(-1);
		allinsurers.add(0);
		LinkedHashMap<Integer,Double> patientratesin=new LinkedHashMap<Integer,Double>();
		double baseET=0.00547945205;
		double startrate=16;
		LinkedHashMap<Integer,Double> costsperhos=new LinkedHashMap<Integer,Double>();
		int inratepayperiod=365;
		int insurernum=0;
		boolean fixedrate=false;
		double coverrate=1;
		double profitmultiplier=1.05;
		double deductible=0.1;
		int deductibleresetperiod=365;
		int periodstoirrelevance=100;
		double oldmult=0.2;
		Insurer insur=new Insurer(gov, baseET,startrate, patientratesin, costsperhos, inratepayperiod
				, insurernum, fixedrate, coverrate, profitmultiplier, deductible,deductibleresetperiod, periodstoirrelevance, oldmult, 5);
		Hospital hos1=new Hospital(hosloc1,gov,startprice,cap,5,initqualdist1,0.5,0,allinsurers);
		Hospital hos2=new Hospital(hosloc2,gov,startprice,cap,5,initqualdist2,0.5,1,allinsurers);
		//hos1=hos1.deepcopyagent();
		ArrayList<Location> hoslocks=new ArrayList<Location>();
		hoslocks.add(hosloc1);
		hoslocks.add(hosloc2);
		int a=randgen.nextInt(); 
		ArrayList<Patient> patients=new ArrayList<Patient>();
		Randomdraws riskaffinitydraw=new Randomdraws(0.7,0.2,"n",randgen.nextInt() );
		for(int k=0;k<1000;k++)//TODO change back to 1000
		{

			Location patloc=new Location(locdraw.draw(),locdraw.draw());
			LinkedHashSet<Integer> hospitals=new LinkedHashSet<Integer>();
			hospitals.add(0);
			hospitals.add(1);
			Randomdraws initqualdist3=new Randomdraws(2,5,"u",a+randgen.nextInt());
			Randomdraws initvardist=new Randomdraws(1,3,"u",a+randgen.nextInt());
			Randomdraws initloyaldist=new Randomdraws(0,1,"u",a+randgen.nextInt());
			Randomdraws initpricedist=new Randomdraws(5,9,"u",a+randgen.nextInt());
			ArrayList<Randomdraws> multiplierdists=new ArrayList<Randomdraws>();;
			for(int i=0;i<6;i++)
			{
				if(i!=3)
				{
					Randomdraws entry=new Randomdraws(0,1,"u",a+randgen.nextInt());
					multiplierdists.add(entry);
				}
				else
				{
					Randomdraws entry=new Randomdraws(100,130,"u",a+randgen.nextInt());
					multiplierdists.add(entry);
				}
			}
			LinkedHashMap<Integer,Double> distancesin=new LinkedHashMap<Integer,Double>();
			LinkedHashMap<Integer, Location> hospitallocks=new LinkedHashMap<Integer,Location>();
			for(int hospital: hospitals)
			{
				hospitallocks.put(hospital, hoslocks.get(hospital));
				distancesin.put(hospital, patloc.calcdist(hoslocks.get(hospital)));
			}

			Randomdraws budgetdraws=new Randomdraws(21,35,"u",a+randgen.nextInt());
			double budget=budgetdraws.draw();
			Patientobjectivefactor initobjfac= new Patientobjectivefactor(hospitals,initqualdist3,
					initvardist,initloyaldist,initpricedist,multiplierdists,distancesin,budget,nocarepenalty);
			double tremblinghandchance=0;
			double nohospitalvar=0.25;
			double ET=0.00547945205;
			Randomdraws tremblehand=new Randomdraws(0,1,"u",randgen.nextInt());
			Patient testpatient=new Patient( patloc, budget,  initobjfac,
					k, gov, hospitals,
					ET, nocarepenalty, 355,riskaffinitydraw.truncateddraw(0, 2),tremblinghandchance,nohospitalvar,tremblehand,365) ;
			//testpatient.setnumperiodsstillinbudget(354);
			//testpatient=testpatient.deepcopyagent();
			patients.add(testpatient);
		}
		int budgettracker=0;
		int negotiationstracker=10;
		int paytracker=0;
		int patvistrack=0;
		int visnohos=0;
		int numvishos2priceperiod2=0;
		double frac=0;
		for(int p=0;p<3700;p++)//TODO set back to 10000
		{

			if(negotiationstracker==deductibleresetperiod)
			{
				for(int z=0;z<patients.size();z++)
				{
					if(patients.get(z).getinsurer()==0)
					{
						insur.checkifpatstillsameET(z);
						boolean worthforpat=patients.get(z).checkifinsurancestillworthit();
						boolean worthforins=insur.patientstillworth(z);
						if(!worthforpat||!worthforins)
						{
							patients.get(z).setnullinsurer();
							insur.removepatient(patients.get(z));
						}
					}
				}
				System.out.println("check"+p);
				//System.out.println("check");
				frac=(double)insur.getpatients().size()/(double)patients.size();
				if(p!=0)
				{
					hos1.changeprice();
					hos2.changeprice();
				}
				boolean accepted1=false;
				boolean accepted2=false;
				for(int i=0;i<3;i++)
				{
					if(!accepted1&&!insur.gethospitals().contains(0))
					{
						double bid1=insur.hospitaloffer(hos1, i, true);
						if(bid1!=Double.MAX_VALUE)
						{
							accepted1=hos1.acceptinsureroffer(insur.getID(), bid1, frac);
							//System.out.println("does hospital one accept?"+accepted1+"with bid"+bid1);
							if(accepted1)
							{
								insur.addhospital(hos1.getID(), bid1);
							}
						}
					}
					else if(!accepted1)
					{
						double renegbid=insur.renegotiate(hos1.getID());

						//System.out.println(p);
						boolean acceptreneghos1=hos1.acceptinsureroffer(insur.getID(), renegbid, frac);
						if(acceptreneghos1)
						{
							insur.addhospital(hos1.getID(), renegbid);
						}
					}
					if(!accepted2&&!insur.gethospitals().contains(1))
					{
						double bid2=insur.hospitaloffer(hos2, i, true);
						if(bid2!=Double.MAX_VALUE)
						{
							accepted2=hos2.acceptinsureroffer(insur.getID(), bid2, 0);
							if(accepted2)
							{
								insur.addhospital(hos2.getID(), bid2);
							}
						}
					}
					else if (!accepted2)
					{
						double renegbid=insur.renegotiate(hos2.getID());

						//System.out.println(p);
						boolean acceptreneghos2=hos2.acceptinsureroffer(insur.getID(), renegbid, frac);
						if(acceptreneghos2)
						{
							insur.addhospital(hos2.getID(), renegbid);
						}
					}
					
				}
				//System.out.println("check2");
				for(int l=0;l<patients.size();l++)
				{
					boolean notaccepted=true;
					int i=0;

					while(notaccepted&&i<3)
					{
						double offeredrate=insur.patientoffer(patients.get(l), i);
						//System.out.println("offer"+offeredrate);
						//System.out.println(i);
						if(offeredrate!=Double.MAX_VALUE)
						{
							//System.out.println("offered rate pat"+offeredrate+"this is max"+Double.MAX_VALUE);
							boolean pataccepts=patients.get(l).acceptinsureroffer(insur, offeredrate, insur.getdeductible());
							if(pataccepts)
							{
								//System.out.println("does this ever happen?");
								insur.patientaccepted(patients.get(l), offeredrate);
								notaccepted=false;
							}
						}
						i++;
					}
				}
				//System.out.println("number of insurer pats"+insur.getpatients().size());
				negotiationstracker=0;
			}
			negotiationstracker++;
			//System.out.println("period"+p);
			for(int l=0;l<patients.size();l++)
			{
				if(patients.get(l).needshospital(randgen))
				{

					if(patients.get(l).getbesthospital()==0)
					{
						boolean vis=hos1.processpatient(patients.get(l));
						if(insur.getpatients().contains(patients.get(l).getID())&&vis)
						{
							//System.out.println(insur.getprices());
							insur.updateaftervisit(patients.get(l).getID(), 0);
						}

					}
					else if(patients.get(l).getbesthospital()==1)
					{
						boolean sucvis=hos2.processpatient(patients.get(l));
						if(insur.getpatients().contains(patients.get(l).getID())&&sucvis)
						{
							//System.out.println(insur.getprices());
							insur.updateaftervisit(patients.get(l).getID(), 1);
						}
						patvistrack++;

						//		patvistrack++;
						if(sucvis)
						{
							numvishos2priceperiod2++;
						}

					}
					else
					{
						visnohos++;
					}
					//System.out.println("patient"+l+"has visited"+visit);
				}

				patients.get(l).updatecurrentmoneyperperiod();

			}
			hos1.updateaftereverytimeperiod();
			hos2.updateaftereverytimeperiod();
			budgettracker++;
			insur.updateprofiteveryperiod();
//			if(budgettracker==10)
//			{
//				for(int l=0;l<patients.size();l++)
//				{
//					if(l==9)
//					{
//						//System.out.println(patients.get(l).getbesthospital());
//					}
//					patients.get(l).resetcurrentmoney();
//				}
//
//				budgettracker=0;
//			}
		}
		//System.out.println(Patient.totalvisits);
		//System.out.println(Patient.disillusioned);
		//System.out.println(Patient.hos1vis);
		System.out.println("patvistrack"+patvistrack);
		//int numpats=0;

		//System.out.println(visnohos);
		System.out.println("insurer patients"+insur.getpatients().size());
		System.out.println("insurer hospitals"+insur.gethospitals());
		System.out.println("insurer prices"+insur.getcosts());
		//System.out.println(hos1.calcnumpatsandtimes(-1,-1));
		System.out.println(hos2.calcnumpatsandtimes(-1,0));
		System.out.println("insurer profit"+insur.gettotalprofit());
		int nohoscounter=0;
		int hos1counter=0;
		int hos2counter=0;
		int lochoscounter1=0;
		int lochoscounter2=0;
		int highdistancenohospats=0;
		int lowincomenohospats=0;
		double avgbestdistance=0;
		for(int i=0;i<patients.size();i++)
		{
			if(patients.get(i).getbesthospital()==0)
			{
				hos1counter++;
			}
			else if(patients.get(i).getbesthospital()==1)
			{
				hos2counter++;
			}
			else
			{
				nohoscounter++;
				if(patients.get(i).getloc().calcdist(hos1.getloc())>3.163905797534862
						&&patients.get(i).getloc().calcdist(hos2.getloc())>3.163905797534862)
				{
					highdistancenohospats++;
				}
				if(patients.get(i).getbudget()<15)
				{
					lowincomenohospats++;
				}
			}
			if(patients.get(i).getloc().calcdist(hos1.getloc())<patients.get(i).getloc().calcdist(hos2.getloc()))
			{
				lochoscounter1++;
				avgbestdistance=((double) i*avgbestdistance)/((double)i+1)+patients.get(i).getloc().calcdist(hos1.getloc())/((double)i+1);
			}
			else
			{
				lochoscounter2++;
				avgbestdistance=((double) i*avgbestdistance)/((double)i+1)+patients.get(i).getloc().calcdist(hos2.getloc())/((double)i+1);


			}
		}
		System.out.println("num patients to prefer hospital 1 "+hos1counter);
		System.out.println("num patients to prefer hospital 2 "+hos2counter);
		System.out.println("num patients to prefer no hospital  "+nohoscounter);
		System.out.println("number of patients that are close to hospital 1 "+lochoscounter1);
		System.out.println("number of patients that are close to hospital 2 "+lochoscounter2);
		System.out.println("number high distance no hospital pats"+highdistancenohospats);
System.out.println("number of low income no hos pats"+lowincomenohospats);
		System.out.println("hospital 1 profit"+hos1.gettotalprofit());
		System.out.println("hospital 2 profit"+hos2.gettotalprofit());
		System.out.println("average best distance"+avgbestdistance);
		System.out.println(hos1.calcnumpatsandtimes(-1, -1));
		System.out.println("hospital1 costs noninsured"+hos1.getprice());
		System.out.println("hospital2 costs noninsured"+hos2.getprice());
		System.out.println("insurer prices to hospitals"+insur.getcosts());

		//System.out.println(insur.getprofithistory());
	}


}


