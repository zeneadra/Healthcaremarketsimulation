package healthcaresimulationpackage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Results {
	private World world;
	public Results(World world)
	{
		
	}

	public void printresultstocsv(String extension)
	{
		try {
			PrintWriter writer_progress = new PrintWriter(".\\results"+extension+".txt", "UTF-8");
			writer_progress.print(23);
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
