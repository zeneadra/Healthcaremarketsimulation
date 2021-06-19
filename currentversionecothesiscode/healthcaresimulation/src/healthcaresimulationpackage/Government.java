package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
/**
 * mostly done may need interface and deepcopy
 * @author miche
 *
 */
public class Government extends Actor implements Deepcopiableactor {

	private LinkedHashMap<Integer,Double> pricemaxes;//maximum price that hospitals can charge
	private LinkedHashSet<Integer> pricerestrictedhospitals;
	private LinkedHashSet<Integer> obligatedinsurers;// set of insurers that have to accept patients
	private LinkedHashSet<Integer> obligatedpatients;// set of patients that have to have insurance
	private LinkedHashMap<Integer,Double> patientnumvisitsinfo;// number of visits per patient
	private LinkedHashMap<Integer,Integer> numtrackedperiodspats;//number of periods that patients are tracked
	private double maxrate; //maximum rate that insurer can ask
	private LinkedHashSet<Integer> informedins;//the set of informed insurers
	private LinkedHashSet<Integer> trackedpatients;// the patients that are tracked
	private LinkedHashSet<Integer> informedpats;//patients that are informed about the hospitals
	private LinkedHashSet<Integer> trackedhospitals;//hospitals which performance is tracked.
	private LinkedHashMap<Integer,ArrayList<Double>> hospitalinfo;//first entry is number of observations  quality and cost it does not contain variance
	public Government( LinkedHashMap<Integer,Double> inpricemaxes,
			LinkedHashSet<Integer> inpricerestrictedhospitals,
			 LinkedHashSet<Integer> inobligatedinsurers,LinkedHashSet<Integer> inobligatedpatients,
			 double inmaxrate,LinkedHashSet<Integer> ininformedins, LinkedHashSet<Integer> intrackedpatients,
			 LinkedHashSet<Integer> ininformedpats,LinkedHashSet<Integer> intrackedhospitals
			 )
	{
		pricemaxes=inpricemaxes;
		pricerestrictedhospitals=inpricerestrictedhospitals;
		obligatedinsurers=inobligatedinsurers;
		obligatedpatients=inobligatedpatients;
		informedins=ininformedins;
		trackedpatients=intrackedpatients;
		informedpats=ininformedpats;
		trackedhospitals=intrackedhospitals;
		patientnumvisitsinfo=new LinkedHashMap<Integer,Double>();
		numtrackedperiodspats=new LinkedHashMap<Integer,Integer>();
		hospitalinfo=new LinkedHashMap<Integer,ArrayList<Double>>();
		for(int i:trackedhospitals)
		{
			ArrayList<Double> infos=new ArrayList<Double>();
			hospitalinfo.put(i, infos);
		}

	}
	
	
	
	
	
	
	/**
	 * for test cases without governement
	 * @param restrictedprice
	 * @param inpricemaxes
	 */
	public Government(boolean restrictedprice, LinkedHashMap<Integer,Double> inpricemaxes )
	{
		pricemaxes=inpricemaxes;
		pricerestrictedhospitals=new LinkedHashSet<Integer>();//TODO REMOVE THIS.
		}
	/**
	 * @return the pricemax
	 */
	public double getPricemax(int hosident) {
		return pricemaxes.get(hosident);
	}
	public boolean hashospitalinfo(int hos)
	{
		boolean hospitalinf=false;
		if(trackedhospitals.contains(hos))
		{
			if(hospitalinfo.get(hos).size()>0)
			{
				hospitalinf=true;
			}
		}
		return hospitalinf;
	}
	public void updatehosinfo(double quality, double price, int hospital)
	{
		if(trackedhospitals.contains(hospital))
		{
			if(hospitalinfo.get(hospital).size()>0)
			{
			ArrayList<Double> info=hospitalinfo.get(hospital);
			info.set(0,info.get(0)+1);
			info.set(1, (info.get(1)*(info.get(0)-1))/info.get(0)+quality/info.get(0));
			info.set(2, (info.get(2)*(info.get(0)-1))/info.get(0)+price/info.get(0));
			}
			else
			{
				ArrayList<Double> info=new ArrayList<Double>();
				info.add((double) 1);
				info.add(quality);
				info.add(price);
				hospitalinfo.put(hospital, info);
			}
		}
	}
	public ArrayList<Double> gethospitalinfo(int hos)
	{
		ArrayList<Double> info=new ArrayList<Double>();
		if(trackedhospitals.contains(hos))
		{
			if(hospitalinfo.get(hos).size()>0)
			{
				info.add(hospitalinfo.get(hos).get(1));//this is correct first entry is number of observations
				info.add(hospitalinfo.get(hos).get(2));
			}
		}
		return info;
	}
	/**
	 * @param pricemax the pricemax to set
	 */
	public void setPricemax(double pricemax, int hosident) {
		pricemaxes.put(hosident, pricemax);
		pricerestrictedhospitals.add(hosident);
	}
	public boolean isinformedpat(int p)
	{
		return informedpats.contains(p);
	}
	public void changeinformedpats(LinkedHashSet<Integer> infopatients, boolean add)
	{
		if(add)
		{
			informedpats.addAll(infopatients);
		}
		else
		{
			informedpats.removeAll(infopatients);
		}
	}
	public void changeinformedinsurers(LinkedHashSet<Integer> infoinsurers, boolean add)
	{
		if(add)
		{
			informedins.addAll(infoinsurers);
		}
		else
		{
			informedins.removeAll(infoinsurers);
		}
	}
	@Override
	public Government deepcopyagent() {
		// TODO Auto-generated method stub
		return new Government(true,deepcopyclass.LHM_I_D(pricemaxes));
	}
	public boolean obligationinsurance(int insurernum) {

		return obligatedinsurers.contains(insurernum);
	}
	public boolean obligatedinsurance(int patientnum) {

		return obligatedpatients.contains(patientnum);
	}
	public boolean haspatientinfo(int p)
	{
		return patientnumvisitsinfo.containsKey(p);
	}
	public double getpatientETinfospec(int p)
	{
		return patientnumvisitsinfo.get(p)/numtrackedperiodspats.get(p);
	}
	public double getmaxrateins(int ins)
	{
		double returnval=Double.MAX_VALUE;
		if(this.obligationinsurance(ins))
		{
			returnval=maxrate;
		}
		return returnval;
	}
	public boolean providesinfoonpattoins(int ins, int p) {
		boolean providesinfo=false;
		//System.out.println("has info?"+patientnumvisitsinfo);
		//System.out.println("is allowed to share"+informedins.contains(ins));
		if(informedins.contains(ins)&&this.haspatientinfo(p))
		{
			providesinfo=true;
		}
		return providesinfo;
	}
	public void updatepatinfo(LinkedHashSet<Integer> patvisitset)
	{
		for(int pats:patvisitset)
		{
			if(patvisitset.contains(pats)&&trackedpatients.contains(pats)&&patientnumvisitsinfo.containsKey(pats))
			{
				patientnumvisitsinfo.put(pats,patientnumvisitsinfo.get(pats)+1);
			}
			else if(trackedpatients.contains(pats)&&patvisitset.contains(pats))
			{
				patientnumvisitsinfo.put(pats,(double) 1);
			}
		}
		for(int pats:trackedpatients)
		{
			if(patvisitset.contains(pats)&&numtrackedperiodspats.containsKey(pats))
			{
				numtrackedperiodspats.put(pats, numtrackedperiodspats.get(pats)+1);
			}
			else if(patvisitset.contains(pats))
			{
				numtrackedperiodspats.put(pats, 1);
			}
		}
	}
	public void changetrackedpats(LinkedHashSet<Integer> newtrackedpats, boolean removal)
	{
		if(!removal)
		{
			trackedpatients.addAll(newtrackedpats);
			for(int pat:newtrackedpats)
			{
				numtrackedperiodspats.put(pat,0);
				patientnumvisitsinfo.put(pat, (double) 0);
			}
		}
		else
		{
			trackedpatients.removeAll(newtrackedpats);
			for(int pat:newtrackedpats)
			{
				numtrackedperiodspats.remove(pat);
				patientnumvisitsinfo.remove(pat);
			}
		}
	}
	public boolean ispricemaxed(int hosident)
	{
		return pricerestrictedhospitals.contains(hosident);
	}
	public void setmaxrate(double inmaxrate)
	{
		maxrate=inmaxrate;
	}

}
