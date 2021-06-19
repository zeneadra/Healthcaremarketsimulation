package healthcaresimulationpackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Initiliazer {

	public static World createworld()
	{
		try {
			File basedatfile=new File(".\\basedat.txt");
			Scanner basereader = new Scanner(basedatfile);
			//ArrayList<ArrayList<String>> CSVreader.readCSVdoubleonly(basedatfile, ",", false);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		char separator=',';
		File regiondatfile=new File(".\\regiondat");
		ArrayList<ArrayList<ArrayList<Double>>> regiondata=new ArrayList<ArrayList<ArrayList<Double>>>();
		//regiondata is formatted as follows the outer arraylist are the regions
		try {
			//This defines the 
		ArrayList<ArrayList<Double>> regiondat=CSVreader.readCSVdoubleonly(regiondatfile, separator, false);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		

	}
	public LinkedHashMap<Integer,Hospital> createemptyhospitals(boolean determinedplaces, 
			Government gov, int numhospitals, ArrayList<ArrayList<Double>> regiondat)
	{
		char separator=',';
		File hoslocdat= new File(".\\hospitallocationdata");
		LinkedHashMap<Integer,Hospital> hospitals=new LinkedHashMap<Integer,Hospital>();
		try {
		ArrayList<ArrayList<Double>> locdat=CSVreader.readCSVdoubleonly(hoslocdat
				, separator, false);
		ArrayList<Location> locs=new ArrayList<Location>();
		if(determinedplaces)// interprit as already given coordinates
		{
			for(int i=0;i<locdat.size();i++)
			{
				Location loc=new Location(locdat.get(i).get(0),locdat.get(i).get(1) );
				locs.add(loc);

			}
		}
		else
		{
			// we first need to get the area distributions
			/**
			 * the areas are defined as rectangular areas with a multiplier
			 * The area multipliers are normalized to one. Areas with no 
			 * muliplier are assumed to have multiplier 1 before normalization.
			 * 
			 */
			
		}
		File hosdat=new File(".\\hospitaldata");
		ArrayList<ArrayList<Double>> vals  =CSVreader.readCSVdoubleonly(hoslocdat
				, separator, false);
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hospitals;
	}
}
