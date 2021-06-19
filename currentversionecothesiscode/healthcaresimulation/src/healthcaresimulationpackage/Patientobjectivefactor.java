package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
//in principe klaar
public class Patientobjectivefactor extends Objective {
	private LinkedHashMap<Integer, Integer> numvisits;// number of visits to each hospital
	private LinkedHashMap<Integer, Double> expectedqualities;//quality term for each hospital
	private LinkedHashMap<Integer, ArrayList<Double>> histqual;//all values for the quality in the past
	private LinkedHashMap<Integer, Double> estimatedstddiffs;//stddiff of quality term in objective function
	private LinkedHashMap<Integer,Double> loyaltyfactors;// loyalty terms in objective function
	private LinkedHashSet<Integer> setofhospitals;
	private LinkedHashSet<Integer> insuredhospitals;// set of insured hospitals
	private LinkedHashMap<Integer,Double> setprices;//prices of insured hospitals after deductible paid
	private LinkedHashMap<Integer,Double> deductiblesetprices;//prices of insured hospitals ebfore deductible has been paid
	private double deductiblepaid;//amount of deductible already paid
	private double deuctibleamount;//total size of deductible
	private ArrayList<Double> multipliers;//multipliers of the objective function
	/**
	 * the multipliers in the multipliers list are
	 *  the multiplier for the following variable in the objective function
	 *  0. is the distance multiplier
	 *  1.is the quality multiplier
	 *  2 and 3 are never both included either the term with the third(2) multiplier is multiplied by zero
	 *   or the term with the fourth(3) multiplier is multiplied by zero
	 *  2. is the price multiplier given that the price is equal or smaller than the budget
	 *  3. is the penalty for if the price is higher than the budget
	 *  4. is the loyalty multiplier
	 *  5. is the multiplier for the stddiff of the quality
	 * 
	 */
	private LinkedHashMap<Integer,Double> estimatedcosts;// what the patient thinks the hospitals costs
	private LinkedHashMap<Integer, ArrayList<Double>>  histcosts;//the costs that were incurred earlier
	private boolean recentlycalculated;// if the newest version has been calculated fips to false if updated and to true if calculated
	private LinkedHashMap<Integer,Double> currentscores; //current objective scores are stored here
	final private LinkedHashMap<Integer,Double> distances;//maps the IDs of the hospitals to their distances from the patient.
	private double budget;// the amount of money a patient wants to spend not what he can spend.
	private double nulloptionpenalty; //equal to the penalty that the patient experiences when he/she not allowed entry
	private double initqual;//initialpre model expected quality
	private double initcost;// initial pre model expected costs
	private int numrejec;//number of rejections
	public Patientobjectivefactor( 
			LinkedHashSet<Integer> insetofhospitals
			,Randomdraws initqualdist
			,Randomdraws initvardist
			,Randomdraws initloyaldist
			,Randomdraws initpricedist,
			ArrayList<Randomdraws> multiplierdists,
			LinkedHashMap<Integer,Double> distancesin,
			double budgetin, double innulloptionpenalty)//makes a new patient objective function based on distributions
	{
		setofhospitals=new LinkedHashSet<Integer>();
		insuredhospitals=new LinkedHashSet<Integer>();
		setofhospitals.addAll(insetofhospitals);
		numvisits=new LinkedHashMap<Integer, Integer>();
		expectedqualities=new LinkedHashMap<Integer, Double>();
		estimatedstddiffs=new LinkedHashMap<Integer, Double>();
		loyaltyfactors=new LinkedHashMap<Integer, Double>();
		estimatedcosts=new LinkedHashMap<Integer, Double>();
		histqual=new LinkedHashMap<Integer,ArrayList<Double>>();
		histcosts=new LinkedHashMap<Integer,ArrayList<Double>>();
		for(int hospital : setofhospitals){
			numvisits.put(hospital, 0);
			initqual=initqualdist.draw();
			expectedqualities.put(hospital,initqual );
			estimatedstddiffs.put(hospital, initvardist.draw());
			loyaltyfactors.put(hospital, initloyaldist.draw());
			initcost=initpricedist.draw();
			estimatedcosts.put(hospital,initcost );
			histqual.put(hospital,new ArrayList<Double>());
			histcosts.put(hospital,new ArrayList<Double>());
		}
		multipliers=new ArrayList<Double>();
		for(int i=0;i<multiplierdists.size();i++)
		{
			multipliers.add(multiplierdists.get(i).draw());
		}
		recentlycalculated=false;
		currentscores=new LinkedHashMap<Integer,Double>();
		distances=distancesin;
		budget=budgetin;
		setprices=new LinkedHashMap<Integer,Double>();
		nulloptionpenalty=innulloptionpenalty;
		deductiblepaid=0;
		deuctibleamount=0;
		deductiblesetprices=new LinkedHashMap<Integer,Double>();
		numrejec=0;
		//System.out.println(estimatedcosts);

	}
	public Patientobjectivefactor(LinkedHashMap<Integer, Integer> innumvisits,
			LinkedHashMap<Integer, Double> inexpectedqualities,
			LinkedHashMap<Integer, ArrayList<Double>> inhistqual,
			LinkedHashMap<Integer, ArrayList<Double>> inhistcosts,
			LinkedHashMap<Integer, Double> inestimatedstddiffs,
			LinkedHashMap<Integer,Double> inloyaltyfactors,
			LinkedHashSet<Integer> insetofhospitals, 
			LinkedHashSet<Integer> insuredhospitalsin,
			LinkedHashMap<Integer,Double> setpricesin,
			LinkedHashMap<Integer,Double>	deductiblesetpricesin,
			double deductiblepaidin,
			double deuctibleamountin,
			ArrayList<Double> inmultipliers,
			LinkedHashMap<Integer,Double> inestimatedcosts,
			LinkedHashMap<Integer,Double> incurrentscores,
			LinkedHashMap<Integer,Double> distancesin,
			double budgetin, double innulloptionpenalty, double ininitqual,double ininitcost, int numrejecin)
	{
		numvisits=innumvisits;
		expectedqualities=inexpectedqualities;
		histqual=inhistqual;
		histcosts=inhistcosts;
		estimatedstddiffs=inestimatedstddiffs;
		loyaltyfactors=inloyaltyfactors;
		setofhospitals=insetofhospitals;
		multipliers=inmultipliers;
		estimatedcosts=inestimatedcosts;
		currentscores=incurrentscores;
		distances=distancesin;
		budget=budgetin;
		insuredhospitals=insuredhospitalsin;
		setprices=setpricesin;
		deductiblesetprices=deductiblesetpricesin;
		deuctibleamount=deuctibleamountin;
		deductiblepaid=deductiblepaidin;
		nulloptionpenalty=innulloptionpenalty;
		initqual=ininitqual;
		initcost=ininitqual;
		numrejec=numrejecin;
	}
	/*
	 * note that this is also used for the information the governement gives
	 */
	public void update(double performance,double cost, int hospital, boolean externalinformation, boolean penalty)
	{
		//int numinteractions=histqual.get(hospital).size();
		if(!penalty&&!externalinformation)
		{
			/**
			 * loyalty is scaled as the square root of the number of visits
			 */
			numvisits.put(hospital, numvisits.get(hospital)+1);
			loyaltyfactors.put(hospital,Math.pow((double)numvisits.get(hospital),0.5) );
		}
		histqual.get(hospital).add(performance);
		if(performance>10)
		{
			System.out.println("too high performance"+performance);
		}
		if(!insuredhospitals.contains(hospital))
		{
			// check if costs should be estimated like this.
			//the plus 2 because the intial impression counts as interaction for the future expectation
			double newcost=this.calcexpectedval(histcosts.get(hospital), initcost);
			histcosts.get(hospital).add(cost);
			estimatedcosts.put(hospital, newcost);
		}
		else
		{
			if(deuctibleamount-deductiblepaid>deductiblesetprices.get(hospital))
			{
				this.updatedeductiblepaid(deductiblesetprices.get(hospital));
			}
			else if(deuctibleamount>deductiblepaid)
			{
				this.updatedeductiblepaid(deuctibleamount-deductiblepaid);
			}
		}
		//the plus 2 because the intial impression counts as interaction for the future expectation
		double newquality=this.calcexpectedval(histqual.get(hospital), initqual);
		expectedqualities.put(hospital, newquality);
		double newstddiff=this.calcstddiff(histqual.get(hospital));
		estimatedstddiffs.put(hospital,newstddiff);

		recentlycalculated=false;
	}
	/**
	 * the multipliers in the multipliers list are
	 *  the multiplier for the following variable in the objective function
	 *  0. is the distance multiplier
	 *  1.is the quality multiplier
	 *  2 and 3 are never both included either the term with the third(2) multiplier is multiplied by zero
	 *   or the term with the fourth(3) multiplier is multiplied by zero
	 *  2. is the price multiplier given that the price is equal or smaller than the budget
	 *  3. is the penalty for if the price is higher than the budget
	 *  4. is the loyalty multiplier
	 *  5. is the multiplier for the stddiff of the quality
	 * 
	 */
	public void calcscores()
	{
		if(!recentlycalculated)
		{
			LinkedHashMap<Integer,Double> costs=	this.getestimatedcostsandprices();
			for(int hospital : setofhospitals){
				double score=-Double.MAX_VALUE;

				if(costs.get(hospital)>budget)
				{
					score=-multipliers.get(0)*distances.get(hospital)
							+multipliers.get(1)*expectedqualities.get(hospital)
							-multipliers.get(2)*costs.get(hospital)
							-multipliers.get(3)
							+multipliers.get(4)*loyaltyfactors.get(hospital)
							-multipliers.get(5)*estimatedstddiffs.get(hospital);
					//					System.out.println(" over budget");
					//					System.out.println("budget"+budget);
					//					System.out.println("all multipliers"+multipliers);
					//					System.out.println("after this the score terms in order");
					//					System.out.println(-multipliers.get(0)*distances.get(hospital));
					//					System.out.println(+multipliers.get(1)*expectedqualities.get(hospital));
					//					System.out.println(-multipliers.get(2)*costs.get(hospital));
					//					System.out.println("estimated costs"+costs);
					//					System.out.println(-multipliers.get(3));
					//					System.out.println(+multipliers.get(4)*loyaltyfactors.get(hospital));
					//					System.out.println(-multipliers.get(5)*estimatedstddiffs.get(hospital));
					//					System.out.println("score"+score);
					if(Double.isNaN(score))
					{
						Loggers.errorlogger("score is NaN");
						double newstddiff=this.calcstddiff(histqual.get(hospital));
						System.out.println("estimatedstddiffs"+newstddiff);
						System.out.println("last"+histqual.get(hospital).get(histqual.get(hospital).size()-1)
								+"second to last"+histqual.get(hospital).get(histqual.get(hospital).size()-2));

					}
					//						if(multipliers.get(0)==3.2900256329914495)
					//						{
					//					System.out.println("hos"+hospital);
					//					System.out.println(1);
					//					System.out.println("score: "+score);
					//												System.out.println("after this the score terms in order");
					//												System.out.println(-multipliers.get(0)*distances.get(hospital));
					//												System.out.println(+multipliers.get(1)*expectedqualities.get(hospital));
					//												System.out.println(-multipliers.get(2)*estimatedcosts.get(hospital));
					//												System.out.println(-multipliers.get(3));
					//												System.out.println(+multipliers.get(4)*loyaltyfactors.get(hospital));
					//												System.out.println(-multipliers.get(5)*estimatedstddiffs.get(hospital));
					//						}
				}
				else
				{
					score=-multipliers.get(0)*distances.get(hospital)
							+multipliers.get(1)*expectedqualities.get(hospital)
							-multipliers.get(2)*costs.get(hospital)
							+multipliers.get(4)*loyaltyfactors.get(hospital)
							-multipliers.get(5)*estimatedstddiffs.get(hospital);
					//					System.out.println("not over budget");
					//					System.out.println("budget"+budget);
					//					System.out.println("all multipliers"+multipliers);
					//					System.out.println("after this the score terms in order");
					//					System.out.println(-multipliers.get(0)*distances.get(hospital));
					//					System.out.println(+multipliers.get(1)*expectedqualities.get(hospital));
					//					System.out.println(-multipliers.get(2)*costs.get(hospital));
					//					System.out.println("estimated costs"+costs);
					//					System.out.println(+multipliers.get(4)*loyaltyfactors.get(hospital));
					//					System.out.println(-multipliers.get(5)*estimatedstddiffs.get(hospital));
					//					System.out.println("score"+score);
					//						if(multipliers.get(0)==3.2900256329914495)
					//						{
					//					System.out.println("hos"+hospital);
					//
					//												System.out.println(2);
					//												System.out.println("score: "+score);
					//												System.out.println("after this the score terms in order");
					//												System.out.println(-multipliers.get(0)*distances.get(hospital));
					//												System.out.println(+multipliers.get(1)*expectedqualities.get(hospital));
					//												System.out.println(-multipliers.get(2)*estimatedcosts.get(hospital));
					//												System.out.println(+multipliers.get(4)*loyaltyfactors.get(hospital));
					//												System.out.println(-multipliers.get(5)*estimatedstddiffs.get(hospital));
					//						}
				}

				currentscores.put(hospital,score);
			}
		}



		recentlycalculated=true;
	}
	public Patientobjectivefactor deepcopy()
	{
		LinkedHashMap<Integer, Integer> numvisitscopy= deepcopyclass.LHM_I_I(numvisits);
		LinkedHashMap<Integer, Double> expectedqualitiescopy=deepcopyclass.LHM_I_D(expectedqualities);
		LinkedHashMap<Integer, ArrayList<Double>> histqualcopy=deepcopyclass.LHM_I_A_D(histqual);
		LinkedHashMap<Integer, ArrayList<Double>> histcostscopy=deepcopyclass.LHM_I_A_D(histcosts);
		LinkedHashMap<Integer, Double> estimatedstddiffscopy=deepcopyclass.LHM_I_D(estimatedstddiffs);
		LinkedHashMap<Integer,Double> loyaltyfactorscopy=deepcopyclass.LHM_I_D(loyaltyfactors);
		LinkedHashSet<Integer> setofhospitalscopy=deepcopyclass.LHSI(setofhospitals);
		LinkedHashSet<Integer> insuredhospitalscopy=deepcopyclass.LHSI(insuredhospitals);
		LinkedHashMap<Integer,Double> setpricescopy=deepcopyclass.LHM_I_D(setprices);
		LinkedHashMap<Integer,Double> deductiblesetpricescopy=deepcopyclass.LHM_I_D(deductiblesetprices);

		ArrayList<Double> multiplierscopy=deepcopyclass.A_D(multipliers);
		LinkedHashMap<Integer,Double> estimatedcostscopy=deepcopyclass.LHM_I_D(estimatedcosts);
		LinkedHashMap<Integer,Double> currentscorescopy=deepcopyclass.LHM_I_D(currentscores);
		LinkedHashMap<Integer,Double> distancescopy=deepcopyclass.LHM_I_D(distances);
		Patientobjectivefactor copy=new Patientobjectivefactor(numvisitscopy,
				expectedqualitiescopy,histqualcopy,histcostscopy,estimatedstddiffscopy,
				loyaltyfactorscopy,setofhospitalscopy,insuredhospitalscopy,setpricescopy,deductiblesetpricescopy,
				deductiblepaid,
				deuctibleamount,multiplierscopy,
				estimatedcostscopy,  currentscorescopy,distancescopy,budget, nulloptionpenalty, initqual, initcost,numrejec);
		return copy;


	}
	@Override
	public LinkedHashMap<Integer,Double> getscores()
	{
		if(!recentlycalculated)
		{
			this.calcscores();
		}
		return currentscores;

	}
	@Override
	public double evaluatefunctionperelement(int element)
	{
		if(!recentlycalculated)
		{
			this.calcscores();
		}
		//System.out.println(currentscores);
		double objectivevalueforelement=currentscores.get(element);
		return objectivevalueforelement;

	}
	@Override
	public int choosebestoption()
	{
		if(!recentlycalculated)
		{
			this.calcscores();

		}
		int besthospital=-1;
		double locmax=-Double.MAX_VALUE;

		for(Map.Entry<Integer,Double> hospital : currentscores.entrySet())
		{
			if(hospital.getValue()>locmax&&hospital.getValue()>-multipliers.get(1)*Math.pow(numrejec+1,2)*nulloptionpenalty)
			{
				locmax=hospital.getValue();
				besthospital=hospital.getKey();
			}
		}
		if(besthospital<0&&-multipliers.get(1)*nulloptionpenalty<-2000)
		{
			//TODO to find this
			Loggers.errorlogger("no hospital preference even though the penalty is severe ");
			//			System.out.println("penalty term: "+-multipliers.get(1));
			//			System.out.println( "overview of quality history: "+this.histqual);
			//			System.out.println( "overview of cost history: "+this.histcosts);
			//			System.out.println("multipliers: "+this.multipliers);
			//			System.out.println(this.getscores());
		}
		return besthospital;
	}
	public int besthosifnovisit()
	{
		if(!recentlycalculated)
		{
			this.calcscores();

		}

		int besthospital=-1;
		double locmax=-Double.MAX_VALUE;

		for(Map.Entry<Integer,Double> hospital : currentscores.entrySet())
		{
			if(hospital.getValue()>locmax)
			{
				locmax=hospital.getValue();

				besthospital=hospital.getKey();
			}
		}
		if(besthospital==-1)
		{
			//System.out.println("these are the current scores"+currentscores);
			//this.calcscores();
			//System.out.println("these are the current scores again "+currentscores);

		}
		return besthospital;
	}
	public void setbudget(double tosetbudget)
	{
		budget=tosetbudget;
		recentlycalculated=false;
	}
	public double getbudget() {
		return budget;
	}
	public double calcstddiff(ArrayList<Double> quallist)
	{
		double meanqual=0;

		for(int i=0;i<quallist.size();i++)
		{
			meanqual=meanqual+quallist.get(i);
		}
		meanqual=meanqual/quallist.size();
		double sumofsquares=0;
		for(int i=0;i<quallist.size();i++)
		{
			double diff=quallist.get(i)-meanqual;
			sumofsquares=sumofsquares+Math.pow(diff, 2)/quallist.size();
		}
		double stddiff=0;
		if(sumofsquares>0.0000001)
		{
			stddiff=Math.pow(sumofsquares,0.5);
		}
		else
		{
			stddiff=0.0000001;
			if(quallist.size()>1)
			{
				//System.out.println("relevant histqual"+histqual);
				//System.out.println("relevant quallist"+quallist);
				//System.out.println(meanqual);
			}
		}
		if(Double.isNaN(stddiff))
		{
			//in active sleep
			//System.out.println(stddiff);
			//System.out.println("sumofsquares"+sumofsquares/quallist.size()+" squared mean "+Math.pow(meanqual,2));
			//								try {
			//									Thread.sleep(20000000);
			//								} catch (InterruptedException e) {
			//									e.printStackTrace();
			//								}
		}
		return stddiff;
	}
	/**
	 * also return fixed prices
	 * @return
	 */
	public LinkedHashMap<Integer,Double> getestimatedcostsandprices()
	{
		LinkedHashMap<Integer,Double> estimatedcostsandprices=new LinkedHashMap<Integer,Double>();
		for(int hospital : setofhospitals)
		{
			if(!insuredhospitals.contains(hospital))
			{
				estimatedcostsandprices.put(hospital, estimatedcosts.get(hospital));
			}
			else if(deductiblepaid<deuctibleamount) 
			{
				if(setprices.get(hospital)+deuctibleamount-deductiblepaid<deductiblesetprices.get(hospital))
				{
					estimatedcostsandprices.put(hospital, deductiblesetprices.get(hospital));
				}
				else
				{
					estimatedcostsandprices.put(hospital, setprices.get(hospital)+deuctibleamount-deductiblepaid);

				}
			}
			else
			{
				estimatedcostsandprices.put(hospital, setprices.get(hospital));
			}
		}
		return estimatedcostsandprices;
	}
	public LinkedHashMap<Integer,Double> getestimatedcosts()
	{

		return estimatedcosts;
	}
	public void setinsurance(LinkedHashSet<Integer> insuredhospitalsin
			,LinkedHashMap<Integer,Double> setpricesin, double deductibleamountins,
			LinkedHashMap<Integer,Double> deductiblesetpricesin)
	{
		//can be more efficient insuredhospitals can be setpricesin.keySet()		
		recentlycalculated=false;
		insuredhospitals=insuredhospitalsin;
		setprices=setpricesin;
		deuctibleamount=deductibleamountins;
		deductiblepaid=0;
		deductiblesetprices=deductiblesetpricesin;

	}
	public double getthirdmult()
	{
		return multipliers.get(2);
	}
	public double getfourthmult()
	{
		return multipliers.get(3);

	}
	public LinkedHashMap<Integer, Double> getprices()
	{
		return setprices;
	}
	public boolean isinsured(int hospital)
	{
		return insuredhospitals.contains(hospital);
	}
	/**
	 * calculated the expected value based on quadratic amortization 
	 * @param vals
	 * @return
	 */
	public double calcexpectedval(ArrayList<Double> vals,double initval)
	{
		double val=0;
		double amortfac=0;
		for(int i=vals.size()-1;i>=0;i--)
		{
			val=val+vals.get(i)/(Math.pow((vals.size()-i),2));
			amortfac=amortfac+1/(Math.pow((vals.size()-i),2));
		}
		amortfac=amortfac+1/(Math.pow((vals.size()+1),2));
		val=val+initval/(Math.pow((vals.size()+1),2));
		double returnval=val/amortfac;
		return returnval;
	}
	public void resetdeductible()
	{
		deductiblepaid=0;
		recentlycalculated=false;
	}
	public void updatedeductiblepaid(double cost)
	{
		deductiblepaid=deductiblepaid+cost;
		recentlycalculated=false;
	}
	public double getpredeductibleprice(int hos)
	{
		//System.out.println(estimatedcosts+"hos"+hos);
		double cost=estimatedcosts.get(hos);
		if(deductiblesetprices.containsKey(hos))
		{
			cost=deductiblesetprices.get(hos);
		}
		return cost;
	}
	public double getpropprice(int hos) {
		double propprice=1;
		if(setprices.containsKey(hos)&&deductiblesetprices.containsKey(hos))
		{
			propprice=setprices.get(hos)/deductiblesetprices.get(hos);
		}
		return propprice;

	}
	public double getrealprice(int hos)
	{
		return this.getestimatedcostsandprices().get(hos);
	}
	public void  updaterejec(int rejec)
	{
		numrejec=rejec;
		recentlycalculated=false;
	}
	public LinkedHashSet<Integer> getinsuredhospitals()
	{
		return insuredhospitals;
	}
	public LinkedHashMap<Integer,Double> getsetprices()
	{
		return setprices;
	}
	public LinkedHashMap<Integer,Double> getdeductiblesetprices()
	{
		return deductiblesetprices;
	}
}
