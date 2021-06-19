package healthcaresimulationpackage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

public class Worldgenerator {

	public static void createworldfromfile( File govfile, ArrayList<File> patientfiles)
	{
	}



	private static Government creategovfromfile(File govfile) {
		//ArrayList<ArrayList<String>> dat=CSVreader.readCSV(govfile, ',', false);
		return null;
	}

	public static LinkedHashMap<Integer,Randomdraws> constructbudgetdraws(double refcosts, Random randgen)
	{
		LinkedHashMap<Integer,Randomdraws> budgetdraws=new LinkedHashMap<Integer,Randomdraws>();
		Randomdraws group1=new Randomdraws(0.3*refcosts,0.6*refcosts,"u",randgen.nextInt());
		budgetdraws.put(1,group1);
		Randomdraws group3=new Randomdraws(0.6*refcosts,refcosts,"u",randgen.nextInt());
		budgetdraws.put(3,group3);
		Randomdraws group5=new Randomdraws(refcosts,1.4*refcosts,"u",randgen.nextInt());
		budgetdraws.put(5,group5);
		Randomdraws group8=new Randomdraws(1.4*refcosts,1.9*refcosts,"u",randgen.nextInt());
		budgetdraws.put(8,group8);
		Randomdraws group11=new Randomdraws(1.9*refcosts,3.35,"p",randgen.nextInt());
		budgetdraws.put(11,group11);
		return budgetdraws;
	}

	public static ArrayList<Double> drawfrombudgetdists(int numdraws,LinkedHashMap<Integer,Randomdraws> budgetdraws
			, int group)
	{
		ArrayList<Double> draws=new ArrayList<Double>();
		if(group==1||group==3||group==5||group==8||group==11)
		{
			for(int i=0;i<numdraws;i++)
			{
				draws.add(budgetdraws.get(group).draw());
			}
		}
		else
		{
			int numgroup1draws=0;
			int numgroup3draws=0;
			int numgroup5draws=0;
			int numgroup8draws=0;
			int numgroup11draws=0;

			if(group==2)
			{
				 numgroup1draws=Math.floorDiv(4*numdraws,10);
				 numgroup3draws=Math.floorDiv(4*numdraws,10);
				 numgroup5draws=Math.floorDiv(2*numdraws,10);
				 numgroup8draws=0;
				 numgroup11draws=0;
				int difffromtotal=numdraws-numgroup1draws-numgroup3draws-numgroup5draws-numgroup8draws-numgroup11draws;
				numgroup3draws=numgroup3draws+difffromtotal;
			}
			if(group==4)
			{
				 numgroup1draws=Math.floorDiv(2*numdraws,10);
				 numgroup3draws=Math.floorDiv(4*numdraws,10);
				 numgroup5draws=Math.floorDiv(4*numdraws,10);
				 numgroup8draws=0;
				 numgroup11draws=0;
				int difffromtotal=numdraws-numgroup1draws-numgroup3draws-numgroup5draws-numgroup8draws-numgroup11draws;
				numgroup3draws=numgroup3draws+difffromtotal;
			}
			if(group==6)
			{
				 numgroup1draws=Math.floorDiv(2*numdraws,10);
				 numgroup3draws=Math.floorDiv(2*numdraws,10);
				 numgroup5draws=Math.floorDiv(2*numdraws,10);
				 numgroup8draws=Math.floorDiv(2*numdraws,10);
				 numgroup11draws=Math.floorDiv(2*numdraws,10);
				int difffromtotal=numdraws-numgroup1draws-numgroup3draws-numgroup5draws-numgroup8draws-numgroup11draws;
				numgroup5draws=numgroup5draws+difffromtotal;

			}
			if(group==7)
			{
				 numgroup1draws=0;
				 numgroup3draws=Math.floorDiv(numdraws,10);
				 numgroup5draws=Math.floorDiv(45*numdraws,100);
				 numgroup8draws=Math.floorDiv(45*numdraws,100);
				 numgroup11draws=0;
				 int difffromtotal=numdraws-numgroup1draws-numgroup3draws-numgroup5draws-numgroup8draws-numgroup11draws;
					numgroup5draws=numgroup5draws+difffromtotal;
			}
			if(group==9)
			{
				 numgroup1draws=0;
				 numgroup3draws=0;
				 numgroup5draws=Math.floorDiv(2*numdraws,10);
				 numgroup8draws=Math.floorDiv(6*numdraws,10);
				 numgroup11draws=Math.floorDiv(2*numdraws,10);
				 int difffromtotal=numdraws-numgroup1draws-numgroup3draws-numgroup5draws-numgroup8draws-numgroup11draws;
				numgroup8draws=numgroup8draws+difffromtotal;


			}
			if(group==10)
			{
				 numgroup1draws=0;
				 numgroup3draws=0;
				 numgroup5draws=0;
				 numgroup8draws=Math.floorDiv(5*numdraws,10);
				 numgroup11draws=Math.floorDiv(5*numdraws,10);
				int difffromtotal=numdraws-numgroup1draws-numgroup3draws-numgroup5draws-numgroup8draws-numgroup11draws;
				numgroup8draws=numgroup8draws+difffromtotal;

			}
			draws.addAll(drawfrombudgetdists( numgroup1draws,budgetdraws, 1));
			draws.addAll(drawfrombudgetdists( numgroup3draws,budgetdraws, 3));
			draws.addAll(drawfrombudgetdists( numgroup5draws,budgetdraws, 5));
			draws.addAll(drawfrombudgetdists( numgroup8draws,budgetdraws, 8));
			draws.addAll(drawfrombudgetdists( numgroup11draws,budgetdraws, 11));

		}
		return draws;
	}
	public static LinkedHashMap<Integer,Randomdraws> constructhealthdraws( Random randgen)
	{
		LinkedHashMap<Integer,Randomdraws> healthdraws=new LinkedHashMap<Integer,Randomdraws>();
		Randomdraws group1=new Randomdraws(0.1/365,0.9/365,"u",randgen.nextInt());
		healthdraws.put(1, group1);
		Randomdraws group2=new Randomdraws(0.9/365,2.1/365,"u",randgen.nextInt());
		healthdraws.put(2, group2);
		Randomdraws group5=new Randomdraws(2.1/365,2.9/365,"u",randgen.nextInt());
		healthdraws.put(5, group5);
		Randomdraws group9=new Randomdraws(2.9/365,7.1/365,"u",randgen.nextInt());
		healthdraws.put(9, group9);
		Randomdraws group10=new Randomdraws(7.1/365,32.9/365,"u",randgen.nextInt());
		healthdraws.put(10, group10);
		return healthdraws;
	}
	public static ArrayList<Double> drawfromhealthdists(int numdraws,LinkedHashMap<Integer,Randomdraws> healthdraws
			, int group)
	{
		ArrayList<Double> draws=new ArrayList<Double>();
		if(group==1||group==2||group==5||group==9||group==10)
		{
			for(int i=0;i<numdraws;i++)
			{
				draws.add(healthdraws.get(group).draw());
			}
		}
		else
		{
			int numgroup1draws=0;
			int numgroup2draws=0;
			int numgroup5draws=0;
			int numgroup9draws=0;
			int numgroup10draws=0;

			if(group==3)
			{
				 numgroup1draws=Math.floorDiv(4*numdraws,10);
				 numgroup2draws=Math.floorDiv(4*numdraws,10);
				 numgroup5draws=Math.floorDiv(numdraws,10);
				 numgroup9draws=Math.floorDiv(5*numdraws,100);
				 numgroup10draws=Math.floorDiv(5*numdraws,100);
				int difffromtotal=numdraws-numgroup1draws-numgroup2draws-numgroup5draws-numgroup9draws-numgroup10draws;
				numgroup2draws=numgroup2draws+difffromtotal;
			}
			if(group==4)
			{
				 numgroup1draws=Math.floorDiv(3*numdraws,10);
				 numgroup2draws=Math.floorDiv(3*numdraws,10);
				 numgroup5draws=Math.floorDiv(25*numdraws,100);
				 numgroup9draws=Math.floorDiv(10*numdraws,100);
				 numgroup10draws=Math.floorDiv(5*numdraws,100);
				int difffromtotal=numdraws-numgroup1draws-numgroup2draws-numgroup5draws-numgroup9draws-numgroup10draws;
				numgroup2draws=numgroup2draws+difffromtotal;
			}
			if(group==6)
			{
				 numgroup1draws=Math.floorDiv(25*numdraws,100);
				 numgroup2draws=Math.floorDiv(25*numdraws,100);
				 numgroup5draws=Math.floorDiv(3*numdraws,10);
				 numgroup9draws=Math.floorDiv(15*numdraws,100);
				 numgroup10draws=Math.floorDiv(5*numdraws,100);
				int difffromtotal=numdraws-numgroup1draws-numgroup2draws-numgroup5draws-numgroup9draws-numgroup10draws;
				numgroup5draws=numgroup5draws+difffromtotal;

			}
			if(group==7)
			{
				 numgroup1draws=Math.floorDiv(numdraws,10);
				 numgroup2draws=Math.floorDiv(2*numdraws,10);
				 numgroup5draws=Math.floorDiv(35*numdraws,100);
				 numgroup9draws=Math.floorDiv(25*numdraws,100);
				 numgroup10draws=Math.floorDiv(10*numdraws,100);
				 int difffromtotal=numdraws-numgroup1draws-numgroup2draws-numgroup5draws-numgroup9draws-numgroup10draws;
					numgroup5draws=numgroup5draws+difffromtotal;
			}
			if(group==8)
			{
				 numgroup1draws=Math.floorDiv(numdraws,10);
				 numgroup2draws=Math.floorDiv(numdraws,10);
				 numgroup5draws=Math.floorDiv(3*numdraws,10);
				 numgroup9draws=Math.floorDiv(30*numdraws,100);
				 numgroup10draws=Math.floorDiv(20*numdraws,100);
				 int difffromtotal=numdraws-numgroup1draws-numgroup2draws-numgroup5draws-numgroup9draws-numgroup10draws;
				 numgroup9draws=numgroup9draws+difffromtotal;


			}
			draws.addAll(drawfromhealthdists( numgroup1draws,healthdraws, 1));
			draws.addAll(drawfromhealthdists( numgroup2draws,healthdraws, 2));
			draws.addAll(drawfromhealthdists( numgroup5draws,healthdraws, 5));
			draws.addAll(drawfromhealthdists( numgroup9draws,healthdraws, 9));
			draws.addAll(drawfromhealthdists( numgroup10draws,healthdraws, 10));

		}
		return draws;
	}
	public static ArrayList<Integer> gethealthdists(int numareas,int seed)
	{
		ArrayList<Integer> healthdists=new ArrayList<Integer>();
		Random dicerand=new Random(seed);
		for(int i=0;i<numareas;i++)
		{
			int dicerol=dicerand.nextInt(5);
			if(dicerol==0)
			{
				healthdists.add(3);
			}
			else if(dicerol==1)
			{
				healthdists.add(4);
			}
			else if(dicerol==2)
			{
				healthdists.add(6);
			}
			else if(dicerol==3)
			{
				healthdists.add(7);
			}
			else if(dicerol==4)
			{
				healthdists.add(8);
			}
			
		}
		return healthdists;
	}
	public static ArrayList<Integer> getincomedists(int numareas,int seed)
	{
		ArrayList<Integer> incomedists=new ArrayList<Integer>();
		Random dicerand=new Random(seed);
		for(int i=0;i<numareas;i++)
		{
			int dicerol=dicerand.nextInt(11);
			incomedists.add(dicerol+1);

			
		}
		return incomedists;
	}
	
}
