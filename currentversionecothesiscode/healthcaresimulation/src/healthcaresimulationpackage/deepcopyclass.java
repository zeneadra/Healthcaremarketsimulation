package healthcaresimulationpackage;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * this method makes safe deep copies of different objects it is coded with abbreviations
 * 
 *A-ArrayList
 *LHM-LinkedHashMap
 *I-Integer
 *D-Double 
 *S-String
 *
 *order of abbreviations is the same as full 
 * @author miche
 *
 */
public class deepcopyclass {

	static public ArrayList<Double> A_D(ArrayList<Double> tocopy)
	{
		ArrayList<Double> copy=new ArrayList<Double>();
		for(int i=0;i<tocopy.size();i++)
		{
			copy.add(tocopy.get(i));
		}
		return copy;
	}
	static public ArrayList<Integer> A_I(ArrayList<Integer> tocopy)
	{
		ArrayList<Integer> copy=new ArrayList<Integer>();
		for(int i=0;i<tocopy.size();i++)
		{
			copy.add(tocopy.get(i));
		}
		return copy;
	}
	static public ArrayList<ArrayList<Double>> A_A_D(ArrayList<ArrayList<Double>> tocopy)
	{
		ArrayList<ArrayList<Double>> copy=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<tocopy.size();i++)
		{
			ArrayList<Double> subcop=A_D(tocopy.get(i));
			copy.add(subcop);
		}
		return copy;
	}
	static public ArrayList<ArrayList<Integer>> A_A_I(ArrayList<ArrayList<Integer>> tocopy)
	{
		ArrayList<ArrayList<Integer>> copy=new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<tocopy.size();i++)
		{
			ArrayList<Integer> subcop=A_I(tocopy.get(i));
			copy.add(subcop);
		}
		return copy;
	}
	static public ArrayList<ArrayList<Integer>> A_A_I_part(ArrayList<ArrayList<Integer>> tocopy, int firstindex, int secondindex)
	{
		ArrayList<ArrayList<Integer>> copy=new ArrayList<ArrayList<Integer>>();
		for(int i=firstindex;i<=secondindex;i++)
		{
			ArrayList<Integer> subcop=A_I(tocopy.get(i));
			copy.add(subcop);
		}
		return copy;
	}
	static public ArrayList<ArrayList<Integer>> A_A_Iwithoutfirstouterlist(ArrayList<ArrayList<Integer>> tocopy)
	{
		ArrayList<ArrayList<Integer>> copy=new ArrayList<ArrayList<Integer>>();
		for(int i=1;i<tocopy.size();i++)
		{
			ArrayList<Integer> subcop=A_I(tocopy.get(i));
			copy.add(subcop);
		}
		return copy;
	}
	static public ArrayList<ArrayList<ArrayList<Double>>> A_A_A_D(ArrayList<ArrayList<ArrayList<Double>>> tocopy)
	{
		ArrayList<ArrayList<ArrayList<Double>>> copy=new ArrayList<ArrayList<ArrayList<Double>>>();
		for(int i=0;i<tocopy.size();i++)
		{
			ArrayList<ArrayList<Double>> subcop=A_A_D(tocopy.get(i));
			copy.add(subcop);
		}
		return copy;
	}
	static public LinkedHashMap<Integer,Integer> LHM_I_I(LinkedHashMap<Integer,Integer>tocopy)
	{
		Iterator<Integer> it=tocopy.keySet().iterator();
		LinkedHashMap<Integer,Integer> copy=new LinkedHashMap<Integer,Integer>();
		while(it.hasNext())
		{
			int key=it.next();
			copy.put(key, tocopy.get(key));
		}
		return copy;
	}
	static public LinkedHashMap<Integer,Double> LHM_I_D(LinkedHashMap<Integer,Double>tocopy)
	{
		Iterator<Integer> it=tocopy.keySet().iterator();
		LinkedHashMap<Integer,Double> copy=new LinkedHashMap<Integer,Double>();
		while(it.hasNext())
		{
			int key=it.next();
			copy.put(key, tocopy.get(key));
		}
		return copy;
	}
	static public LinkedHashMap<Integer,ArrayList<Integer>> LHM_I_A_I(LinkedHashMap<Integer,ArrayList<Integer>>tocopy)
	{
		Iterator<Integer> it=tocopy.keySet().iterator();
		LinkedHashMap<Integer,ArrayList<Integer>> copy=new LinkedHashMap<Integer,ArrayList<Integer>>();
		while(it.hasNext())
		{
			int key=it.next();
			ArrayList<Integer> subcop=A_I(tocopy.get(key));
			copy.put(key, subcop);
		}
		return copy;
	}
	static public ArrayList<LinkedHashMap<Integer,Integer>> A_LHM_I_I(ArrayList<LinkedHashMap<Integer,Integer>> tocopy)
	{
		ArrayList<LinkedHashMap<Integer,Integer>> copy= new ArrayList<LinkedHashMap<Integer,Integer>>();
		for(int i=0; i<tocopy.size();i++)
		{
			copy.add(deepcopyclass.LHM_I_I(tocopy.get(i)));
		}
		return copy;
	}
	static public LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>> LHM_I_A_A_I(LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>>tocopy)
	{
		Iterator<Integer> it=tocopy.keySet().iterator();
		LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>> copy=new LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>>();
		while(it.hasNext())
		{
			int key=it.next();
			ArrayList<ArrayList<Integer>> subcop=A_A_I(tocopy.get(key));
			copy.put(key, subcop);
		}
		return copy;
	}
	static public LinkedHashMap<Integer,ArrayList<ArrayList<Double>>> LHM_I_A_A_D(LinkedHashMap<Integer,ArrayList<ArrayList<Double>>>tocopy)
	{
		Iterator<Integer> it=tocopy.keySet().iterator();
		LinkedHashMap<Integer,ArrayList<ArrayList<Double>>> copy=new LinkedHashMap<Integer,ArrayList<ArrayList<Double>>>();
		while(it.hasNext())
		{
			int key=it.next();
			ArrayList<ArrayList<Double>> subcop=A_A_D(tocopy.get(key));
			copy.put(key, subcop);
		}
		return copy;
	}
	static public LinkedHashMap<Integer,ArrayList<Double>> LHM_I_A_D(LinkedHashMap<Integer,ArrayList<Double>>tocopy)
	{
		Iterator<Integer> it=tocopy.keySet().iterator();
		LinkedHashMap<Integer,ArrayList<Double>> copy=new LinkedHashMap<Integer,ArrayList<Double>>();
		while(it.hasNext())
		{
			int key=it.next();
			ArrayList<Double> subcop=A_D(tocopy.get(key));
			copy.put(key, subcop);
		}
		return copy;
	}
	static public LinkedHashSet<Integer> LHSI(LinkedHashSet<Integer> tocopy)
	{
		Iterator<Integer> it=tocopy.iterator();
		LinkedHashSet<Integer> copy=new LinkedHashSet<Integer>();
		while(it.hasNext())
		{
			copy.add(it.next());
		}
		return copy;
	}
}
