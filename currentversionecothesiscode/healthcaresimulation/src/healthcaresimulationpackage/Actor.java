package healthcaresimulationpackage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public abstract class Actor {
	
	public void print() {
		try {
			PrintWriter writer_gen_KPI = new PrintWriter(".\\objectidentifiers.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public int getID()
	{
		return 0;
	}

}
