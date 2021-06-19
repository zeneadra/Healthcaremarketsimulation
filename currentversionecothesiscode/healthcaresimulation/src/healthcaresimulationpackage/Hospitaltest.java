package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

public class Hospitaltest {

	public static void main(String[] args) {
		int seed=7534;
		double nocarepenalty=100;
		Random randgen=new Random(seed);
		LinkedHashMap<Integer,Double> empty=new LinkedHashMap<Integer,Double>();
		Government gov=new Government(false,empty);
		Randomdraws locdraw=new Randomdraws(0,10,"u",randgen,seed);

			Location hosloc1=new Location(2.5,3.5);
			Location hosloc2=new Location(3.5,2.5);
			Randomdraws initqualdist1=new Randomdraws(2,5,"u",randgen.nextInt());
			Randomdraws initqualdist2=new Randomdraws(2,5,"u",randgen.nextInt());
			int cap=500;
			double startprice=7;
			LinkedHashSet<Integer> allinsurers=new LinkedHashSet<Integer>();
			allinsurers.add(-1);
			Hospital hos1=new Hospital(hosloc1,gov,startprice,cap,5,initqualdist1,30,0,allinsurers);
			Hospital hos2=new Hospital(hosloc2,gov,startprice,cap,5,initqualdist2,30,1,allinsurers);
			//hos1=hos1.deepcopyagent();
			ArrayList<Location> hoslocks=new ArrayList<Location>();
			hoslocks.add(hosloc1);
			hoslocks.add(hosloc2);
			int a=randgen.nextInt(); 
			ArrayList<Patient> patients=new ArrayList<Patient>();
			for(int k=0;k<100000;k++)
			{

				Location patloc=new Location(locdraw.draw(),locdraw.draw());
				LinkedHashSet<Integer> hospitals=new LinkedHashSet<Integer>();
				hospitals.add(0);
				hospitals.add(1);
				Randomdraws initqualdist3=new Randomdraws(2,5,"u",a+randgen.nextInt());
				Randomdraws initvardist=new Randomdraws(1,3,"u",a+randgen.nextInt());
				Randomdraws initloyaldist=new Randomdraws(0,1,"u",a+randgen.nextInt());
				Randomdraws initpricedist=new Randomdraws(2,6,"u",a+randgen.nextInt());
				ArrayList<Randomdraws> multiplierdists=new ArrayList<Randomdraws>();;
				for(int i=0;i<6;i++)
				{
					if(i!=5)
					{
					Randomdraws entry=new Randomdraws(2,7,"u",a+randgen.nextInt());
					multiplierdists.add(entry);
					}
					else
					{
						Randomdraws entry=new Randomdraws(0,1,"u",a+randgen.nextInt());
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
				
				Randomdraws budgetdraws=new Randomdraws(10,12,"u",a+randgen.nextInt());
				double budget=budgetdraws.draw();
				Patientobjectivefactor initobjfac= new Patientobjectivefactor(hospitals,initqualdist3,
						initvardist,initloyaldist,initpricedist,multiplierdists,distancesin,budget,nocarepenalty);
				double tremblinghandchance=0;
				double nohospitalvar=0.25;
				Randomdraws tremblehand=new Randomdraws(0,1,"u",randgen.nextInt());
				Patient testpatient=new Patient( patloc, budget,  initobjfac,
						k, gov, hospitals,
						0.05, nocarepenalty, 10,0.5,tremblinghandchance,nohospitalvar,tremblehand,10) ;
				//testpatient=testpatient.deepcopyagent();
				patients.add(testpatient);
			}
			int budgettracker=0;
			int patvistrack=0;
			int visnohos=0;
			int numvishos2priceperiod2=0;
			for(int p=0;p<100;p++)
			{
				//System.out.println("period"+p);
				for(int l=0;l<patients.size();l++)
				{
					if(patients.get(l).needshospital(randgen))
					{

						if(patients.get(l).getbesthospital()==0)
						{
							 hos1.processpatient(patients.get(l));

						}
						else if(patients.get(l).getbesthospital()==1)
						{
							boolean sucvis=hos2.processpatient(patients.get(l));
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
				if(budgettracker==10)
				{
					for(int l=0;l<patients.size();l++)
					{
						if(l==9)
						{
							//System.out.println(patients.get(l).getbesthospital());
						}
						patients.get(l).resetcurrentmoney();
					}
					hos1.changeprice();
					hos2.changeprice();
					budgettracker=0;
					System.out.println(p);
				}
			}
			//System.out.println(Patient.totalvisits);
			//System.out.println(Patient.disillusioned);
			//System.out.println(Patient.hos1vis);
			System.out.println(patvistrack);
			//int numpats=0;
			
			//System.out.println(visnohos);
			System.out.println(numvishos2priceperiod2);
			System.out.println(hos1.calcnumpatsandtimes(-1,-1));
			System.out.println(hos2.calcnumpatsandtimes(-1,-1));
	}

}
