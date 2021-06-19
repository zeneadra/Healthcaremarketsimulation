package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import org.apache.commons.math3.distribution.ParetoDistribution;
import org.apache.commons.math3.random.MersenneTwister;

/**
 *  This class provides sampling for multiple distributions
 *  use this class
 * @author Michel Hofmeijer
 *
 */
public class Randomdraws {
	private double param1;
	private double param2; 
	private double param3;
	private String type;
	private int seed;
	private Random randgen;
	private int numdraws;
	private ParetoDistribution paretodist;
	public Randomdraws(double inparam1, double inparam2, 
			double inparam3,
			String intype,int inseed)
	{
		type=intype;
		seed=inseed;
		randgen=new Random(seed);
		setParam1(inparam1);
		setParam2(inparam2); 
		setParam3(inparam3);
		numdraws=0;
	}
	/**
	 * deepcopy intializer
	 * @param inparam1
	 * @param inparam2
	 * @param inparam3
	 * @param intype
	 * @param inseed
	 */
	public Randomdraws(double inparam1, double inparam2, 
			double inparam3,Random inrandgen,
			String intype,int inseed, int innumdraws)
	{
		type=intype;
		seed=inseed;
		randgen=inrandgen;
		setParam1(inparam1);
		setParam2(inparam2); 
		setParam3(inparam3);
		numdraws=innumdraws;

	}
	public Randomdraws(double inparam1, double inparam2,
			String intype,int inseed)
	{
		type=intype;
		seed=inseed;
		randgen=new Random(seed);
		setParam1(inparam1);
		setParam2(inparam2); 
		setParam3(0);
		numdraws=0;
		if(type.equalsIgnoreCase("pareto")
				||type.equalsIgnoreCase("p")
				||type.equalsIgnoreCase("par"))
		{		
		MersenneTwister rangen=new MersenneTwister(seed);
		paretodist=new ParetoDistribution(rangen,inparam1,inparam2);
		}
	} 
	public Randomdraws(double inparam1,
			String intype,int inseed)
	{
		type=intype;
		seed=inseed;
		randgen=new Random(seed);
		setParam1(inparam1);
		setParam2(0); 
		setParam3(0);
		numdraws=0;

	} 
	public Randomdraws(double inparam1, double inparam2, 
			double inparam3,
			String intype,Random inrand, int inseed)
	{
		type=intype;
		randgen= inrand;
		setParam1(inparam1);
		setParam2(inparam2); 
		setParam3(inparam3);
		numdraws=0;
		seed=inseed;

	}
	public Randomdraws(double inparam1, double inparam2,
			String intype,Random inrand, int inseed)
	{
		type=intype;
		randgen= inrand;
		setParam1(inparam1);
		setParam2(inparam2); 
		setParam3(0);
		numdraws=0;
		seed=inseed;
	} 
	public Randomdraws(double inparam1,
			String intype,Random inrand, int inseed)
	{
		type=intype;
		randgen= inrand;
		setParam1(inparam1);
		setParam2(0); 
		setParam3(0);
		numdraws=0;
		seed=inseed;
	} 

	public double draw()
	{
		double output=0;		
		if(type.equalsIgnoreCase("uniform")
				||type.equalsIgnoreCase("u")
				||type.equalsIgnoreCase("uni"))
		{
			output=this.drawuniform();
		}
		else if(type.equalsIgnoreCase("normal")
				||type.equalsIgnoreCase("n")
				||type.equalsIgnoreCase("norm"))
		{
			output=this.drawnormal();
		}
		else if(type.equalsIgnoreCase("exponential")
				||type.equalsIgnoreCase("e")
				||type.equalsIgnoreCase("exp"))
		{
			output=this.drawexponential();
		}
		else if(type.equalsIgnoreCase("pareto")
				||type.equalsIgnoreCase("p")
				||type.equalsIgnoreCase("par"))
		{
			output=this.drawpareto();
		}
		numdraws++;
		return output;
	}
	public double truncateddraw(double lowerborder,double higherborder)
	{
		double output=0;		
		if(type.equalsIgnoreCase("uniform")
				||type.equalsIgnoreCase("u")
				||type.equalsIgnoreCase("uni"))
		{
			output=this.drawuniform();
		}
		else if(type.equalsIgnoreCase("normal")
				||type.equalsIgnoreCase("n")
				||type.equalsIgnoreCase("norm"))
		{
			output=this.drawnormal();
		}
		else if(type.equalsIgnoreCase("exponential")
				||type.equalsIgnoreCase("e")
				||type.equalsIgnoreCase("exp"))
		{
			output=this.drawexponential();
		}
		else if(type.equalsIgnoreCase("pareto")
				||type.equalsIgnoreCase("p")
				||type.equalsIgnoreCase("par"))
		{
			output=this.drawpareto();
		}
		numdraws++;
		if(output<lowerborder||output>higherborder)
		{
			output=this.truncateddraw(lowerborder, higherborder);
		}
		return output;
	}
	public ArrayList<Double> drawmultiple(int numdraws)
	{
		ArrayList<Double> output=new ArrayList<Double>();
		for(int i=0;i<numdraws;i++)
		{
			output.add(this.draw());
		}
		return output;
	}
	/**
	 * param1 is the mean
	 * param2 is the standard deviation
	 * @return returns normal distributed 
	 */
	private double drawnormal()
	{
		return randgen.nextGaussian()*param2+param1;
	}
	/**
	 *  param1 is the maximum of the uniform distribution
	 *  param2 is the minimum of the unifrom distribution
	 * @return
	 */
	private double drawuniform()
	{
		return randgen.nextDouble()*(param1-param2)+param2;
	}
	/**
	 * param1 is the lambda parameter which is the inverse of the mean
	 * @return
	 */
	private double drawexponential()
	{
		return  (Math.log(1-randgen.nextDouble()))/(-param1);
	}
	/**
	 * @return the param1
	 */
	public double getParam1() {
		return param1;
	}
	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(double param1) {
		this.param1 = param1;
	}
	/**
	 * @return the param2
	 */
	public double getParam2() {
		return param2;
	}
	/**
	 * @param param2 the param2 to set
	 */
	public void setParam2(double param2) {
		this.param2 = param2;
	}
	/**
	 * @return the param3
	 */
	public double getParam3() {
		return param3;
	}
	/**
	 * @param param3 the param3 to set
	 */
	public void setParam3(double param3) {
		this.param3 = param3;
	}
	/**
	 * @inseed the seed to set
	 */
	public void setseed(int inseed) {
		this.seed = inseed;
	}
	/**
	 * @return the seed
	 */
	public double getseed() {
		return seed;
	}
	public Randomdraws deepcopy()
	{
		Random newrandgen=new Random(seed);
		for(int i=0;i<numdraws;i++)
		{
			if(type.equalsIgnoreCase("normal")
					||type.equalsIgnoreCase("n")
					||type.equalsIgnoreCase("norm"))
			{
				newrandgen.nextGaussian();
			}
			else
			{
				newrandgen.nextDouble();
			}
		}
		Randomdraws copy=new Randomdraws( param1, param2, 
				param3,newrandgen,
				type,seed,numdraws);
		return copy;
		
	}
	public int randomselection(LinkedHashSet<Integer> set)
	{
		int setsize=set.size();
		int selected=0;
		double randval=this.draw();
		double incrementor=0;
		boolean chosen=false;
		Iterator<Integer> hosit=set.iterator();
		while(hosit.hasNext()&&!chosen)
		{
			incrementor=incrementor+1/((double)setsize);
			if(randval<incrementor)
			{
				selected=hosit.next();
				chosen=true;
			}
			else
			{
				hosit.next();
			}
		}
		return selected;
	}
	public static double calcbinomialchance(double chanceofsuc, int numtrials, int numsuccesses)
	{
		double noverkval=noverk(numtrials,numsuccesses);
		double chance=noverkval* Math.pow(chanceofsuc, numsuccesses)*Math.pow(1-chanceofsuc, numtrials-numsuccesses);
		return chance;
	}
	
	public static int fac(int a)
	{
		int z=1;
		for(int i=1;i<=a;i++)
		{
			z=z*i;
		}
		return z;
	}
	public static int noverk(int n, int k)
	{
		//TODO is probably working
		int ans=1;
		if(k>1)
		{
			int prevans=noverk(n-1,k-1);
			ans=((n*prevans)/k);
			//System.out.println("noverk: "+prevans);
			//System.out.println("n: "+n);
			//System.out.println("k: "+k);
		}
		else
		{
			ans=n;
		}

		return ans;
	}
	public static double binomcumchancetill(double chanceofsuc,int numtrials,int numsucc)
	{
		//TODO doesn't work
		double cumulativechance=0;
		for(int i=0;i<=numsucc;i++)
		{
			cumulativechance=cumulativechance+calcbinomialchance(chanceofsuc, numtrials,i);
			//System.out.println("chance of at most "+i+"successes,is"+cumulativechance);
		}
		return cumulativechance;
	}
	public double drawpareto()
	{
		return paretodist.sample();
	}
}

