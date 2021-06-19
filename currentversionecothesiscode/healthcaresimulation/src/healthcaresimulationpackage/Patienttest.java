package healthcaresimulationpackage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

public class Patienttest {

	/**
	 * only patient testing done further testing only in relation to other classes
	 * @param args
	 */
	public static void main(String[] args) {
		int z=0;
		int x=0;
		Random randgen=new Random(753464);
		for(int j=0;j<1;j++)
		{
			int a=randgen.nextInt();
		Location patloc=new Location(2.5,3.5);
		LinkedHashSet<Integer> hospitals=new LinkedHashSet<Integer>();
		hospitals.add(0);
		hospitals.add(1);
		Randomdraws initqualdist=new Randomdraws(2,5,"u",a+randgen.nextInt());
		Randomdraws initvardist=new Randomdraws(1,3,"u",a+randgen.nextInt());
		Randomdraws initloyaldist=new Randomdraws(0,1,"u",a+randgen.nextInt());
		Randomdraws initpricedist=new Randomdraws(2,6,"u",a+randgen.nextInt());
		ArrayList<Randomdraws> multiplierdists=new ArrayList<Randomdraws>();;
		for(int i=0;i<6;i++)
		{
			Randomdraws entry=new Randomdraws(0,5,"u",a+randgen.nextInt());
			multiplierdists.add(entry);
		}
		Randomdraws lochosgen=new Randomdraws(0,5,"u",randgen.nextInt());
		LinkedHashMap<Integer,Double> distancesin=new LinkedHashMap<Integer,Double>();
		LinkedHashMap<Integer, Location> hospitallocks=new LinkedHashMap<Integer,Location>();
		for(int hospital: hospitals)
		{
			Location hosloc=new Location(lochosgen.draw(),lochosgen.draw());
			hospitallocks.put(hospital, hosloc);
			distancesin.put(hospital, patloc.calcdist(hosloc));
		}
		Patientobjectivefactor initobjfac= new Patientobjectivefactor(hospitals,initqualdist,
				initvardist,initloyaldist,initpricedist,multiplierdists,distancesin,10.5,10);
		LinkedHashMap<Integer,Double> empty=new LinkedHashMap<Integer,Double>();
		double tremblinghandchance=0;
		double nohospitalvar=0.25;
		Randomdraws tremblehand=new Randomdraws(0,1,"u",randgen.nextInt());
		Government gov=new Government(false,empty);
		Patient testpatient=new Patient( patloc, 10.5,  initobjfac,
			0, gov, hospitals,
			0.3, (double)10, 10,0.5,tremblinghandchance,nohospitalvar,tremblehand,10 ) ;
		int best1=testpatient.getbesthospital();
		z=z+best1;
		testpatient.updatepatientaftervisit(true, 3, 4, best1, false);
		testpatient.updatepatientaftervisit(true,3, 4, best1, false);
		if(best1==testpatient.getbesthospital())
		{
			x++;
		}
		testpatient.resetcurrentmoney();
		System.out.println(testpatient);
		}
		System.out.println(z);
		System.out.println(x);
		System.out.println(2.5/3);
	}

}
