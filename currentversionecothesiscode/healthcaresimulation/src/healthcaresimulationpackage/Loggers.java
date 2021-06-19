package healthcaresimulationpackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class Loggers {
	private ArrayList<String> status=new ArrayList<String>();

	public  void logstatus(String currentpos,String overallprog)
	{
		if(status.size()>1)
		{
			status.set(0, overallprog);
			status.set(1,currentpos);
		}
		else
		{
			status.add(overallprog);
			status.add( currentpos);
		}
	}
	public void logstatus(String currentpos)
	{
		if(status.size()>1)
		{
			status.set(1,currentpos);
		}
	}
	public void printstatus()
	{
		Loggers.errorlogger("current status is:"+status);
	}
	public static void clearerrorlogger()
	{
		PrintWriter writer_progress;
		try {
			writer_progress = new PrintWriter(".\\errorlogger.txt", "UTF-8");
			writer_progress.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void clearprogresslogger()
	{
		PrintWriter writer_progress;
		try {
			writer_progress = new PrintWriter(".\\progresslogger.txt", "UTF-8");
			writer_progress.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void clearmessagelogger()
	{
		PrintWriter writer_progress;
		try {
			writer_progress = new PrintWriter(".\\messages.txt", "UTF-8");
			writer_progress.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void progresslogger(String progresstext)
	{
		try {
			File progressfile=new File(".\\progresslogger.txt");
			Scanner progreader = new Scanner(progressfile);
			ArrayList<String> prevprogread= new ArrayList<String>();
			while(progreader.hasNextLine())
			{
				
				prevprogread.add(progreader.nextLine());
			}
			progreader.close();
			prevprogread.add(progresstext);
			PrintWriter writer_progress = new PrintWriter(".\\progresslogger.txt", "UTF-8");
			for(int i=0;i<prevprogread.size();i++) {
				writer_progress.println(prevprogread.get(i));
			}
			writer_progress.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void messagelogger(String progresstext)
	{
		try {
			File progressfile=new File(".\\messages.txt");
			Scanner progreader = new Scanner(progressfile);
			ArrayList<String> prevprogread= new ArrayList<String>();
			while(progreader.hasNextLine())
			{
				
				prevprogread.add(progreader.nextLine());
			}
			progreader.close();
			prevprogread.add(progresstext);
			PrintWriter writer_progress = new PrintWriter(".\\messages.txt", "UTF-8");
			for(int i=0;i<prevprogread.size();i++) {
				writer_progress.println(prevprogread.get(i));
			}
			writer_progress.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void errorlogger(String errortext)
	{
		try {
			File errorfile=new File(".\\errorlogger.txt");
			Scanner progreader = new Scanner(errorfile);
			ArrayList<String> prevprogread= new ArrayList<String>();
			while(progreader.hasNextLine())
			{
				
				prevprogread.add(progreader.nextLine());
			}
			progreader.close();
			prevprogread.add(errortext);
			PrintWriter writer_progress = new PrintWriter(".\\errorlogger.txt", "UTF-8");
			for(int i=0;i<prevprogread.size();i++) {
				writer_progress.println(prevprogread.get(i));
			}
			writer_progress.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
