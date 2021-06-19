package healthcaresimulationpackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class CSVreader {

	/**
	 * currently not used, is used in teh case there is a second value delimiter
	 * @param inputfile
	 * @param separator
	 * @param valuedelimiter
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ArrayList<ArrayList<String>> readCSV(File inputfile,char separator,char valuedelimiter) throws FileNotFoundException
	{
		ArrayList<ArrayList<String>> vals=new ArrayList<ArrayList<String>>();
		Scanner CSVscanner= new Scanner(inputfile);
		CSVscanner.useLocale(Locale.US);

		while(CSVscanner.hasNextLine())
		{
			String fullline= CSVscanner.nextLine();
			ArrayList<String> fulllinetrans=new ArrayList<String>();
			if(fullline!=null)
			{
				int sizeofline=fullline.length();
				int pos=0;
				boolean delimiteractive=false;
				String stringtoadd="";
				while(pos<sizeofline)
				{
					char invest=fullline.charAt(pos);

					if(invest==valuedelimiter)
					{
						if(pos==0)
						{
							delimiteractive=true;
						}
						else if(delimiteractive)
						{
							delimiteractive=false;
						}
						else
						{
							delimiteractive=true;
						}
					}
					else if(!delimiteractive&&invest==separator)
					{
						fulllinetrans.add(stringtoadd);
						stringtoadd="";
					}
					else if(invest!=separator)
					{
						stringtoadd=stringtoadd+invest;
					}
					else if(pos==sizeofline-1)
					{
						stringtoadd=stringtoadd+invest;
						fulllinetrans.add(stringtoadd);
					}
					
					pos++;
				}
			}
			vals.add(fulllinetrans);
		}
		CSVscanner.close();
		return vals;
	}
	/**
	 * Currently used autoremoves the header
	 * @param inputfile
	 * @param separator
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ArrayList<ArrayList<String>> readCSV(File inputfile,char separator, boolean header) throws FileNotFoundException
	{
		ArrayList<ArrayList<String>> vals=new ArrayList<ArrayList<String>>();
		Scanner CSVscanner= new Scanner(inputfile);
		CSVscanner.useLocale(Locale.US);
		if(header&&CSVscanner.hasNextLine())
		{
			String headerline= CSVscanner.nextLine();

		}
		while(CSVscanner.hasNextLine())
		{
			String fullline= CSVscanner.nextLine();
			ArrayList<String> fulllinetrans=new ArrayList<String>();
			if(fullline!=null)
			{
				int sizeofline=fullline.length();
				int pos=0;
				String stringtoadd="";
				while(pos<sizeofline)
				{
					char invest=fullline.charAt(pos);

					if(pos==0)
					{
							stringtoadd=stringtoadd+invest;

					}
					else if(invest==separator)
					{
						fulllinetrans.add(stringtoadd);
						stringtoadd="";
					}
					else if(invest!=separator&&pos!=sizeofline-1)
					{
						stringtoadd=stringtoadd+invest;

					}
					else if(pos==sizeofline-1)
					{
						stringtoadd=stringtoadd+invest;
						fulllinetrans.add(stringtoadd);


					}
					
					pos++;
				}
			}
			vals.add(fulllinetrans);
		}
		CSVscanner.close();
		return vals;
	}
	public static ArrayList<ArrayList<Double>> readCSVdoubleonly(File inputfile,char separator, boolean header) throws FileNotFoundException
	{
		ArrayList<ArrayList<Double>> vals=new ArrayList<ArrayList<Double>>();
		Scanner CSVscanner= new Scanner(inputfile);
		CSVscanner.useLocale(Locale.US);
		if(header&&CSVscanner.hasNextLine())
		{
			String headerline= CSVscanner.nextLine();

		}
		while(CSVscanner.hasNextLine())
		{
			String fullline= CSVscanner.nextLine();
			ArrayList<Double> fulllinetrans=new ArrayList<Double>();
			if(fullline!=null)
			{
				int sizeofline=fullline.length();
				int pos=0;
				String stringtoadd="";
				while(pos<sizeofline)
				{
					char invest=fullline.charAt(pos);

					if(pos==0)
					{
							stringtoadd=stringtoadd+invest;

					}
					else if(invest==separator)
					{
						fulllinetrans.add(Double.parseDouble(stringtoadd));
						stringtoadd="";
					}
					else if(invest!=separator&&pos!=sizeofline-1)
					{
						stringtoadd=stringtoadd+invest;

					}
					else if(pos==sizeofline-1)
					{
						stringtoadd=stringtoadd+invest;
						fulllinetrans.add(Double.parseDouble(stringtoadd));


					}
					
					pos++;
				}
			}
			vals.add(fulllinetrans);
		}
		CSVscanner.close();
		return vals;
	}
}
