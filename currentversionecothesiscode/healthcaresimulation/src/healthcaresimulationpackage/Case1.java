package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

public class Case1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int seed=728347912;
		Loggers.clearerrorlogger();
		double basecosts=42.1790061022;
		int numpats=30000;
		int numins=6;
		int numhos=3;
		boolean hospriceres=true;
		boolean nodistpref=false;
		boolean obligatedinsurersonegov=false;
		boolean obligatedinsurersall=false;
		boolean obligatepatients=false;
		boolean informallins=false;
		boolean informinsurersonegov=false;
		boolean informallpatients=false;
		LinkedHashMap<Integer,Double> inpricemaxes=new LinkedHashMap<Integer,Double>();
		LinkedHashSet<Integer> inpricerestrictedhospitals=new LinkedHashSet<Integer>();
		if(hospriceres)
		{
			for(int i=0;i<numhos;i++)
			{
				inpricemaxes.put(i, (double) 14);
				inpricerestrictedhospitals.add(i);
			}

		}
		LinkedHashSet<Integer> inobligatedinsurers=new LinkedHashSet<Integer>();
		if(obligatedinsurersall)
		{
			for(int i=0;i<numins;i++)
			{
				inobligatedinsurers.add(i);
			}
		}
		if(obligatedinsurersonegov)
		{
			inobligatedinsurers.add(0);
		}
		LinkedHashSet<Integer> inobligatedpatients=new LinkedHashSet<Integer>();
		if(obligatepatients)
		{
			for(int i=0;i<numpats;i++)
			{
				inobligatedpatients.add(i);
			} 
		}
		double inmaxrate=Double.MAX_VALUE;

		LinkedHashSet<Integer> ininformedins=new LinkedHashSet<Integer>();
		if(informallins)
		{
			for(int i=0;i<numins;i++)
			{
				ininformedins.add(i);
			}
		}
		if(informinsurersonegov)
		{
			ininformedins.add(0);
		}
		LinkedHashSet<Integer> intrackedpatients=new LinkedHashSet<Integer>();
		if(informallins||informinsurersonegov)
		{
			for(int i=0;i<numpats;i++)
			{
				intrackedpatients.add(i);
			} 
		}
		LinkedHashSet<Integer> ininformedpats=new LinkedHashSet<Integer>();
		LinkedHashSet<Integer> intrackedhospitals=new LinkedHashSet<Integer>();
		if(informallpatients)
		{
			for(int i=0;i<numpats;i++)
			{
				ininformedpats.add(i);
			} 
			for(int i=0;i<numhos;i++)
			{
				intrackedhospitals.add(i);
			}
		}
		Government gov=new Government(inpricemaxes,inpricerestrictedhospitals,inobligatedinsurers,
				inobligatedpatients,inmaxrate,ininformedins,intrackedpatients,
				ininformedpats,intrackedhospitals);	
		gov.setmaxrate(30);
		Random randgen=new Random(seed);
		LinkedHashSet<Integer> hospitalset=new LinkedHashSet<Integer>();
		LinkedHashMap<Integer,LinkedHashSet<Integer>> hospitalneighbourhoods=new LinkedHashMap<Integer,LinkedHashSet<Integer>>();
		for(int i=0;i<3;i++)
		{
			hospitalset.add(i);
			LinkedHashSet<Integer> neighbourhood=new LinkedHashSet<Integer>();
			hospitalneighbourhoods.put(i, neighbourhood);


		}
		LinkedHashSet<Integer> insurerset=new LinkedHashSet<Integer>();
		for(int i=0;i<6;i++)
		{
			insurerset.add(i);
		}
		LinkedHashMap<Integer,Randomdraws> budgetdraws=Worldgenerator.constructbudgetdraws(basecosts, randgen);
		LinkedHashMap<Integer,Randomdraws> healthdraws=Worldgenerator.constructhealthdraws( randgen);
		int xdirecresetpoint=5;
		int xmin=0;
		int ymin=0;
		Randomdraws riskaffinitydraw=new Randomdraws(0.7,0.2,"n",randgen.nextInt() );
		Randomdraws nocarepenaltydraw=new Randomdraws(60,120,"u",randgen.nextInt() );

		ArrayList<Integer> budgetdists=Worldgenerator.getincomedists(25, randgen.nextInt());
		ArrayList<Integer> healthdists=Worldgenerator.gethealthdists(25, randgen.nextInt());
		//System.out.println("budgets");
		//System.out.println(budgetdists);
		//System.out.println("healths");
		//System.out.println(healthdists);
		int pattracker=0;
		Location hosloc0=new Location(1.5,1.5);//area 6
		Location hosloc1=new Location(2.5,2.5);//area 12
		Location hosloc2=new Location(3.5,3.5);//area 18
		ArrayList<Hospital> hospitals=new ArrayList<Hospital>();
		Randomdraws inithos0qualdist=new Randomdraws(2,6,"u",randgen.nextInt() );
		Randomdraws inithos1qualdist=new Randomdraws(4,8,"u",randgen.nextInt() );
		Randomdraws inithos2qualdist=new Randomdraws(3,7,"u",randgen.nextInt() );
		Hospital hos0=new Hospital(hosloc0,gov,7,180,3,inithos0qualdist,15,0,insurerset);
		hospitals.add(hos0);
		Hospital hos1=new Hospital(hosloc1,gov,13,180,5,inithos1qualdist,15,1,insurerset);
		hospitals.add(hos1);
		Hospital hos2=new Hospital(hosloc2,gov,10,120,4,inithos2qualdist,10,2,insurerset);
		hospitals.add(hos2);
		ArrayList<Patient> patients=new ArrayList<Patient>();
		double totalhealth=0;
		double totalincome=0;
		for(int i=0;i<25;i++)
		{
			if(i!=3&&i!=7&&i!=11&&i!=13&&i!=19)
			{
				Randomdraws xlocationdrawer=new Randomdraws(xmin,xmin+1,"u",randgen.nextInt());
				Randomdraws ylocationdrawer=new Randomdraws(ymin,ymin+1,"u",randgen.nextInt());
				int numregionalpats=750;
				ArrayList<Double> budgets=Worldgenerator.drawfrombudgetdists(numregionalpats, budgetdraws, budgetdists.get(i));
				ArrayList<Double> healths=Worldgenerator.drawfromhealthdists(numregionalpats, healthdraws, healthdists.get(i));
				for(int j=0;j<numregionalpats;j++)
				{
					double nocarepenalty=nocarepenaltydraw.draw();
					double budget=budgets.get(j);
					Location loc=new Location(xlocationdrawer.draw(),ylocationdrawer.draw());
					LinkedHashMap<Integer,Double> distancesin=new LinkedHashMap<Integer,Double>();
					for(int hospital: hospitalset)
					{
						distancesin.put(hospital, loc.calcdist(hospitals.get(hospital).getloc()));
						if(distancesin.get(hospital)<5)
						{
							hospitalneighbourhoods.get(hospital).add(pattracker);
						}

					}

					Randomdraws patqualdist=new Randomdraws(2,8,"u",randgen.nextInt());
					Randomdraws initvardist=new Randomdraws(1,3,"u",randgen.nextInt());
					Randomdraws initloyaldist=new Randomdraws(0,1,"u",randgen.nextInt());
					Randomdraws initpricedist=new Randomdraws(7,13,"u",randgen.nextInt());
					ArrayList<Randomdraws> multiplierdists=new ArrayList<Randomdraws>();;

					for(int k=0;k<6;k++)
					{
						if(k!=3&&k!=0)
						{
							Randomdraws entry=new Randomdraws(0,1,"u",randgen.nextInt());
							multiplierdists.add(entry);
						}
						else if(k==3)
						{
							//penalty for over budget
							Randomdraws entry=new Randomdraws(100,130,"u",randgen.nextInt());
							multiplierdists.add(entry);
						}
						else
						{
							//
							if(!nodistpref)
							{
								Randomdraws entry=new Randomdraws(5,20,"u",randgen.nextInt());
								multiplierdists.add(entry);
							}
							else
							{
								Randomdraws entry=new Randomdraws(0,0,"u",randgen.nextInt());
								multiplierdists.add(entry);	
							}
						}
					}
					totalincome=totalincome+budget;
					Patientobjectivefactor initobjfac= new Patientobjectivefactor(hospitalset,patqualdist,
							initvardist,initloyaldist,initpricedist,multiplierdists,distancesin,budget,nocarepenalty);
					totalhealth=totalhealth+healths.get(j);
					Randomdraws tremblehand=new Randomdraws(0,1,"u",randgen.nextInt());
					Patient patient=new Patient( loc, budget,  initobjfac,
							pattracker, gov, hospitalset,
							healths.get(j),nocarepenalty , 364,riskaffinitydraw.truncateddraw(0.05, 2),0.02,0.02,tremblehand,365) ;
					patients.add(patient);
					pattracker++;
				}
			}
			else
			{
				int numregionalpats=3000;
				Randomdraws xlocationdrawer=new Randomdraws(xmin+0.5,0.25,"n",randgen.nextInt());
				Randomdraws ylocationdrawer=new Randomdraws(ymin+0.5,0.25,"n",randgen.nextInt());
				ArrayList<Double> budgets=Worldgenerator.drawfrombudgetdists(numregionalpats, budgetdraws, budgetdists.get(i));
				ArrayList<Double> healths=Worldgenerator.drawfromhealthdists(numregionalpats, healthdraws, healthdists.get(i));
				for(int j=0;j<numregionalpats;j++)
				{
					double nocarepenalty=nocarepenaltydraw.draw();
					double budget=budgets.get(j);
					Location loc=new Location(xlocationdrawer.truncateddraw(xmin, xmin+1),ylocationdrawer.truncateddraw(ymin, ymin+1));
					LinkedHashMap<Integer,Double> distancesin=new LinkedHashMap<Integer,Double>();
					for(int hospital: hospitalset)
					{
						distancesin.put(hospital, loc.calcdist(hospitals.get(hospital).getloc()));
						if(distancesin.get(hospital)<5)
						{
							hospitalneighbourhoods.get(hospital).add(pattracker);
						}

					}

					Randomdraws patqualdist=new Randomdraws(2,8,"u",randgen.nextInt());
					Randomdraws initvardist=new Randomdraws(1,3,"u",randgen.nextInt());
					Randomdraws initloyaldist=new Randomdraws(0,1,"u",randgen.nextInt());
					Randomdraws initpricedist=new Randomdraws(7,13,"u",randgen.nextInt());
					ArrayList<Randomdraws> multiplierdists=new ArrayList<Randomdraws>();;

					for(int k=0;k<6;k++)
					{
						if(k!=3&&k!=0)
						{
							Randomdraws entry=new Randomdraws(0,1,"u",randgen.nextInt());
							multiplierdists.add(entry);
						}
						else if(k==3)
						{
							//penalty for over budget
							Randomdraws entry=new Randomdraws(100,130,"u",randgen.nextInt());
							multiplierdists.add(entry);
						}
						else
						{
							//
							if(!nodistpref)
							{
								Randomdraws entry=new Randomdraws(5,20,"u",randgen.nextInt());
								multiplierdists.add(entry);
							}
							else
							{
								Randomdraws entry=new Randomdraws(0,0,"u",randgen.nextInt());
								multiplierdists.add(entry);	
							}
						}
					}
					totalincome=totalincome+budget;

					Patientobjectivefactor initobjfac= new Patientobjectivefactor(hospitalset,patqualdist,
							initvardist,initloyaldist,initpricedist,multiplierdists,distancesin,budget,nocarepenalty);
					totalhealth=totalhealth+healths.get(j);

					Randomdraws tremblehand=new Randomdraws(0,1,"u",randgen.nextInt());
					Patient patient=new Patient( loc, budget,  initobjfac,
							pattracker, gov, hospitalset,
							healths.get(j),nocarepenalty , 365,riskaffinitydraw.truncateddraw(0.05, 2),0.02,0.02,tremblehand,365) ;
					patients.add(patient);

					pattracker++;

				}
			}
			xmin++;
			if(xmin==xdirecresetpoint)
			{
				xmin=0;
				ymin++;
			}
		}
		ArrayList<Insurer> insurers=new ArrayList<Insurer>();
		double baseET=totalhealth/patients.size();
		double baseincome=totalincome/patients.size();
		System.out.println(baseET);
		System.out.println(baseincome);
		double deductiblebase=16;
		double avgcosts=42.1790061022;
		LinkedHashMap<Integer,Double> patientratesin0=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> costsperhos0=new LinkedHashMap<Integer,Double>();
		Insurer insur0=new Insurer(gov, baseET,1.05*(avgcosts-deductiblebase), patientratesin0, costsperhos0, 365
				, 0, false, 1, 1.05,deductiblebase ,365, 180, 0.5, 5);
		insurers.add(insur0);
		double deductible1=8;
		LinkedHashMap<Integer,Double> patientratesin1=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> costsperhos1=new LinkedHashMap<Integer,Double>();
		Insurer insur1=new Insurer(gov, baseET,1.05*(avgcosts-deductible1), patientratesin1, costsperhos1, 365
				, 1, false, 1, 1.05,deductible1 ,365, 180, 0.5, 5);
		insurers.add(insur1);
		double deductible2=0;
		LinkedHashMap<Integer,Double> patientratesin2=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> costsperhos2=new LinkedHashMap<Integer,Double>();
		Insurer insur2=new Insurer(gov, baseET,1.05*(avgcosts-deductible2), patientratesin2, costsperhos2, 365
				, 2, false, 1, 1.05,deductible2 ,365, 180, 0.5, 5);
		insurers.add(insur2);
		double coverrate3=0.8;
		double lowdeductible=4;
		LinkedHashMap<Integer,Double> patientratesin3=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> costsperhos3=new LinkedHashMap<Integer,Double>();
		Insurer insur3=new Insurer(gov, baseET,coverrate3*1.05*(avgcosts-lowdeductible), patientratesin3, costsperhos3, 365
				, 3, false, coverrate3, 1.05,lowdeductible ,365, 180, 0.5, 5);
		insurers.add(insur3);
		LinkedHashMap<Integer,Double> patientratesin4=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> costsperhos4=new LinkedHashMap<Integer,Double>();
		double coverrate4=0.7;
		Insurer insur4=new Insurer(gov, baseET,coverrate4*1.05*(avgcosts-lowdeductible), patientratesin4, costsperhos4, 365
				, 4, false, coverrate4, 1.05,lowdeductible ,365, 180, 0.5, 5);
		insurers.add(insur4);
		LinkedHashMap<Integer,Double> patientratesin5=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer,Double> costsperhos5=new LinkedHashMap<Integer,Double>();
		double coverrate5=0.5;

		Insurer insur5=new Insurer(gov, baseET,coverrate5*1.05*(avgcosts-lowdeductible), patientratesin5, costsperhos5, 365
				, 5, false, coverrate5, 1.05,lowdeductible ,365, 180, 0.5, 5);
		insurers.add(insur5);
		//0.01155589208279977*365 average number of visits
		World world=new World(patients, hospitals, insurers, gov, 365);
		world.run(3650,783457934,365,hospitalneighbourhoods);
	}

}
