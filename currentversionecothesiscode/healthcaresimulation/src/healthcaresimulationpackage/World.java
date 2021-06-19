package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
//import java.util.Map;
import java.util.Random;

public class World {
	private ArrayList<Patient> patients;
	private ArrayList<Hospital> hospitals;
	private ArrayList<Insurer> insurers;
	private Government gov;
	private int NPTB;
	private Random randgen;

	/**
	 * number of periods between insurer and patient negotiations and insurer and
	 */
	/**
	 * 
	 * @param inpatients
	 * @param inhospitals
	 * @param ininsurers
	 * @param ingov
	 * @param inNPTB
	 */
	public World(ArrayList<Patient> inpatients,
			ArrayList<Hospital> inhospitals,
			ArrayList<Insurer> ininsurers,
			Government ingov, int inNPTB)
	{
		patients=inpatients;
		hospitals=inhospitals; 
		insurers=ininsurers;
		gov=ingov;
		NPTB=inNPTB;
	}
	public LinkedHashMap<Integer,ArrayList<ArrayList<Double>>> run(int numtimeperiods,
			int seed, int numtimeperiodsbetweeninfo, LinkedHashMap<Integer,LinkedHashSet<Integer>> hosneighbourhoodpats)
	{
		randgen=new Random(seed);
		LinkedHashMap<Integer,ArrayList<ArrayList<Double>>> results=new  LinkedHashMap<Integer,ArrayList<ArrayList<Double>>>();
		/**
		 * results encoding works in the following way.
		 */
		int negotiationstracker=0;
		int infotracker=0;
//		int numrejvis=0;
//		int numvisitstotal=0;
//		int needvisbutprefnohos=0;
		int actualvis=0;
		double totalpaidbypatientsforvisits=0;
		double totalpaidbyinsurersforvisits=0;
//		double profitforhos=0;
		//int moneytracker=1;
//		double beforesimmoney=0;
//		for(int p=0;p<patients.size();p++)
//		{
//			beforesimmoney=beforesimmoney+patients.get(p).getcurrentmoney();
//
//		}
//		System.out.println(beforesimmoney);
		for(int t=0;t<numtimeperiods;t++)
		{
			//System.out.println(t);
			negotiationstracker++;
			infotracker++;

			if(infotracker==numtimeperiodsbetweeninfo)
			{
//			//	moneytracker++;
//				//double totalmoney=30000*62.8040385756693*moneytracker;
//				double actualtotalmoney=0;
//				for(int p=0;p<patients.size();p++)
//				{
//					actualtotalmoney=actualtotalmoney+patients.get(p).getcurrentmoney();
//					//if(patients.get(p).getcurrentmoney()>moneytracker*patients.get(p).getbudget())
//					//{
//						//System.out.println("patient with too much money");
//					//}
//				}
//				//for(int i=0;i<hospitals.size();i++)
//				{
//					actualtotalmoney=actualtotalmoney+hospitals.get(i).gettotalprofit();
//
//				}
//				for(int i=0;i<insurers.size();i++)
//				{
//					actualtotalmoney=actualtotalmoney+insurers.get(i).gettotalprofit();
//				}
//				//System.out.println("should be this much "+totalmoney+" is this much "+actualtotalmoney);
				for(int p=0;p<patients.size();p++)
				{
					if(gov.isinformedpat(p))
					{
						for(int h=0;h<hospitals.size();h++)
						{
							if(gov.hashospitalinfo(h))
							{
								ArrayList<Double> hosinfo=gov.gethospitalinfo(h);
								patients.get(p).updatepatientaftervisit(true,hosinfo.get(0) , hosinfo.get(1), h, true);
							}
						}
					}
				}
				infotracker=0;
			}
			if(negotiationstracker==NPTB)
			{
				for(int i=0;i<insurers.size();i++)
				{
					insurers.get(i).setETtomean();
					insurers.get(i).setbaseratetomean();
				}
				System.out.println("check time"+t);

				if(t!=0)
				{
					for(int i=0;i<hospitals.size();i++)
					{
						hospitals.get(i).changeprice();
						LinkedHashMap<Integer,Boolean> stillworth=hospitals.get(i).insurersstillworth();
						//System.out.println(stillworth);
						for(int ins=0;ins<insurers.size();ins++)
						{
							if(stillworth.containsKey(ins))
							{
								if(!stillworth.get(ins))
								{
									insurers.get(ins).removehospital(hospitals.get(i).getID());
									hospitals.get(i).removeinsurer(ins,t);
								}
							}
						}
					}
				}
				for(int z=0;z<patients.size();z++)
				{
					if(patients.get(z).getinsurer()!=-1)
					{
						int ins=patients.get(z).getinsurer();
						insurers.get(ins).checkifpatstillsameET(z);
						boolean worthforpat=patients.get(z).checkifinsurancestillworthit();
						boolean worthforins=insurers.get(ins).patientstillworth(z);
						if(!worthforpat||!worthforins)
						{
							patients.get(z).setnullinsurer();
							insurers.get(ins).removepatient(patients.get(z));
						}
					}
				}
				//we have to check if the insurers and hospitals are large providers
				LinkedHashMap<Integer,LinkedHashMap<Integer,Double>> inshosfracs=new LinkedHashMap<Integer,LinkedHashMap<Integer,Double>>();
				for(int i=0;i<insurers.size();i++)
				{
					LinkedHashMap<Integer,Double> hosfracs=new LinkedHashMap<Integer,Double>();
					for(int j=0;j<hospitals.size();j++)
					{
						double numcontractedlocalpats=0;
						for(int z:insurers.get(i).getpatients())
						{
							if(hosneighbourhoodpats.get(j).contains(z))
							{
								numcontractedlocalpats++;
							}
						}
						double frac=numcontractedlocalpats/hosneighbourhoodpats.get(j).size();
						hosfracs.put(j, frac);
					}
					inshosfracs.put(i, hosfracs);
				}
				LinkedHashMap<Integer,Boolean> hoslargeprovidersbool=new LinkedHashMap<Integer,Boolean>();
				for(int j=0;j<hospitals.size();j++)
				{
					double numvisitedlocalpats=0;
					for(int z:hospitals.get(j).getrecentpatsforfrac())
					{
						if(hosneighbourhoodpats.get(j).contains(z))
						{
							numvisitedlocalpats++;
						}
					}
					double frac=numvisitedlocalpats/hosneighbourhoodpats.get(j).size();
					if(frac>=0.25)
					{
						hoslargeprovidersbool.put(j, true);
					}
					else
					{
						hoslargeprovidersbool.put(j,false);
					}
				}
				//System.out.println("check");
				for(int i=0;i<insurers.size();i++)
				{
					//System.out.println(insurers.size());
					if(i==2)
					{
						//	System.out.println("2 has contract+"+insurers.get(i).gethospitals());
					}
					for(int j=0;j<hospitals.size();j++)
					{
						boolean accept=false;
						int bidcounter=0;

						while(!insurers.get(i).gethospitals().contains(j)&&bidcounter<3)
						{
							double bid1=insurers.get(i).hospitaloffer(hospitals.get(j), bidcounter, hoslargeprovidersbool.get(j));
							//System.out.println("hospital"+hospitals.get(j).getID()+"insurer"+insurers.get(i).getID()+"bid1"+bid1);
							if(bid1!=Double.MAX_VALUE)
							{
								accept=hospitals.get(j).acceptinsureroffer(insurers.get(i).getID(), bid1, inshosfracs.get(i).get(j));
								//System.out.println("does hospital one accept?"+accepted1+"with bid"+bid1);
								if(accept)
								{
									//System.out.println("hospital"+hospitals.get(j).getID()+"accepted the offer of"+insurers.get(i).getID());
									//System.out.println("hospital 0"+hospitals.get(0).contractedinsurers);
									insurers.get(i).addhospital(hospitals.get(j).getID(), bid1);
								}
							}
							bidcounter++;
						}
						while(!accept&&bidcounter<3&&insurers.get(i).gethospitals().contains(j))
						{
							double bid1=insurers.get(i).renegotiate(hospitals.get(j).getID());
							//System.out.println("hospital"+hospitals.get(j).getID()+"insurer"+insurers.get(i).getID()+"bid1"+bid1);
							if(bid1!=Double.MAX_VALUE)
							{
								accept=hospitals.get(j).acceptinsureroffer(hospitals.get(j).getID(), bid1, inshosfracs.get(i).get(j));
								//System.out.println("does hospital one accept?"+accepted1+"with bid"+bid1);
								if(accept)
								{
									//System.out.println("hospital"+hospitals.get(j).getID()+"accepted the offer of"+insurers.get(i).getID());
									insurers.get(i).addhospital(hospitals.get(j).getID(), bid1);
								}
							}
							bidcounter++;
						}
					}
					//System.out.println("check2");
					for(int l=0;l<patients.size();l++)
					{
						boolean notaccepted=true;
						int z=0;
						while(notaccepted&&z<4)
						{
							double offeredrate=Double.MAX_VALUE;
							if(patients.get(l).getinsurer()!=insurers.get(i).getID())
							{
							 offeredrate=insurers.get(i).patientoffer(patients.get(l), z);
							}
							else
							{
								offeredrate=insurers.get(i).patrenegotiate(patients.get(l), z);
							}
							if(Double.isNaN(offeredrate))
							{
								int wasnotreneg=patients.get(l).getinsurer();
								System.out.println("something is wrong! pat ins?"+wasnotreneg);
							}

							//insurers.get(i).patrenegotiate(p, numrejections)
							//System.out.println("offer"+offeredrate);
							if(offeredrate!=Double.MAX_VALUE)
							{
								//System.out.println("offered rate pat"+offeredrate+"from ins"+insurers.get(i).getID());
								int initinsure=patients.get(l).getinsurer();
								boolean pataccepts=patients.get(l).acceptinsureroffer(insurers.get(i), offeredrate, insurers.get(i).getdeductible());
								if(pataccepts)
								{
									//System.out.println("does this ever happen?");
									insurers.get(i).patientaccepted(patients.get(l), offeredrate);
									notaccepted=false;
									if(initinsure!=-1&&initinsure!=insurers.get(i).getID())
									{
										insurers.get(initinsure).removepatient(patients.get(l));
									}
								}
							}
							z++;
						}
					}
					//System.out.println("number of insurer pats"+insur.getpatients().size());
					//System.out.println("pat ins negos time "+diff+" ins "+i+" time "+t);
					negotiationstracker=0;
				}


			}

			LinkedHashSet<Integer> patvisitset=new LinkedHashSet<Integer>();
			for(int l=0;l<patients.size();l++)
			{
				if(patients.get(l).needshospital(randgen))
				{
					int besthos=patients.get(l).getbesthospital();
					//numvisitstotal++;
					if(besthos!=-1)
					{
						double beforemoney=patients.get(l).getcurrentmoney();
						boolean vis=hospitals.get(besthos).processpatient(patients.get(l));
						if(vis)
						{
							patvisitset.add(patients.get(l).getID());
							actualvis++;
							totalpaidbypatientsforvisits=totalpaidbypatientsforvisits+(beforemoney-patients.get(l).getcurrentmoney());
						}
						else
						{
							//numrejvis++;
						}
						if(patients.get(l).getinsurer()!=-1&&vis)
						{
							//System.out.println(insur.getprices());
							double beforeprofit=insurers.get(patients.get(l).getinsurer()).getprofit();
							insurers.get(patients.get(l).getinsurer()).
							updateaftervisit(patients.get(l).getID(), besthos);
							totalpaidbyinsurersforvisits=totalpaidbyinsurersforvisits+(beforeprofit-insurers.get(patients.get(l).getinsurer()).getprofit());
						}
					}
					else {
						//needvisbutprefnohos++;
					}
				}
			}
			//double diff=0;
			for(int i=0;i<patients.size();i++)
			{
				//double prevmon=patients.get(i).getcurrentmoney();
				patients.get(i).updatecurrentmoneyperperiod();
				//diff=patients.get(i).getcurrentmoney()-prevmon;
			}			
			//System.out.println(diff);

			gov.updatepatinfo(patvisitset);
			for(int i=0;i<insurers.size();i++)
			{
				//System.out.println(insurers.get(i).getprofit()+"num pats"+insurers.get(i).getpatients().size());
				insurers.get(i).updateprofiteveryperiod();
			}
			for(int i=0;i<hospitals.size();i++)
			{
				hospitals.get(i).updateaftereverytimeperiod();
			}
			// remove this after testing this is not supposed to be here axcept in one case
//			if(t==1823)
//			{
//				for(int i=0;i<3000;i++)
//				{
//					double oldET=patients.get(i).getchanceofhospital();
//					patients.get(i).setchanceofhospital(oldET+0.1);
//				}
//			}
		}
		int totalinsured=0;
		int nohoscounter=0;
		double avginsurrate=0;
		for(int i=0;i<patients.size();i++)
		{
			if(patients.get(i).getbesthospital()==-1)
			{
				nohoscounter++;
			}
			if(patients.get(i).getinsurer()!=-1)
			{
			avginsurrate=avginsurrate+patients.get(i).insurerrate-insurers.get(patients.get(i).getinsurer()).patientrates.get(i);
			}
		}
		System.out.println("avginsurrate"+avginsurrate);
		for(int i=0;i<insurers.size();i++)
		{
			totalinsured=totalinsured+insurers.get(i).getpatients().size();
		}
		System.out.println("totalinnsured"+totalinsured);
		System.out.println("num patients to prefer no hospital  "+nohoscounter);
		double totalhospitalprofit=0;
		for(int i=0;i<hospitals.size();i++)
		{
		System.out.println("hospital "+hospitals.get(i).getID()+" profit "+hospitals.get(i).gettotalprofit());
		totalhospitalprofit=totalhospitalprofit+hospitals.get(i).gettotalprofit();
		System.out.println("hospital "+hospitals.get(i).getID()+" insurer prices "+hospitals.get(i).insurerprices);
		System.out.println("hospital "+hospitals.get(i).getID()+" noinsprice "+hospitals.get(i).getprice());

		}
		double totalinsurerprofit=0;
		for(int i=0;i<insurers.size();i++)
		{
			totalinsurerprofit=totalinsurerprofit+insurers.get(i).gettotalprofit();
			System.out.println("insurer "+insurers.get(i).getID()+" profit "+insurers.get(i).gettotalprofit());
			System.out.println("insurer "+insurers.get(i).getID()+"costs"+insurers.get(i).getcosts());
			System.out.println("insurer "+insurers.get(i).getID()+" num pats "+insurers.get(i).getpatients().size());

		}
		System.out.println("tothosprofit"+totalhospitalprofit);
		System.out.println("totinsprofit"+totalinsurerprofit);

		System.out.println("actual vis"+actualvis);
		double avgmoney=0;
		int numpatsindebt=0;
		double budgettotal=0;
		for(int i=0;i<patients.size();i++)
		{
			avgmoney=avgmoney+patients.get(i).getcurrentmoney();
			if(patients.get(i).getcurrentmoney()<0)
			{
				numpatsindebt++;
			}
			budgettotal=budgettotal+patients.get(i).getbudget();
		}
		System.out.println("budgettotal"+budgettotal);
		avgmoney=avgmoney/patients.size();
		System.out.println("average patient money"+avgmoney+"numpats in debt"+numpatsindebt);
		return results;
	}
	public World deepcopy()
	{
		ArrayList<Patient> copypatients=new ArrayList<Patient>();
		for(int i=0;i<patients.size();i++)
		{
			copypatients.add(patients.get(i).deepcopyagent());
		}
		ArrayList<Hospital> copyhospitals=new ArrayList<Hospital>();
		for(int i=0;i<hospitals.size();i++)
		{
			copyhospitals.add(hospitals.get(i).deepcopyagent());
		}
		ArrayList<Insurer> copyinsurers=new ArrayList<Insurer>();
		for(int i=0;i<insurers.size();i++)
		{
			copyinsurers.add(insurers.get(i).deepcopyagent());
		}
		World returnworld=new World(copypatients, copyhospitals,copyinsurers,gov.deepcopyagent(), NPTB);
		return returnworld;
	}

	public Patient getpatient(int patientid)
	{
		return patients.get(patientid);
	}


	public Hospital gethospital(int hospital) {
		return hospitals.get(hospital);
	}

	public void updateallagentsendofperiod() {
		//gov.updateendofperiod();
		for(int i=0;i<patients.size();i++)
		{
			patients.get(i).updatecurrentmoneyperperiod();
		}
		for(int i=0;i<hospitals.size();i++)
		{
			hospitals.get(i).updateaftereverytimeperiod();
		}
		for(int i=0;i<insurers.size();i++)
		{
			insurers.get(i).updateprofiteveryperiod();
		}
	}

	public void provideinfotopats() {
		for(int i=0;i<patients.size();i++)
		{
			patients.get(i).processgovernementoffer();
		}

	}

}
