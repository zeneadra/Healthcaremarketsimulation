package healthcaresimulationpackage;

import java.util.LinkedHashMap;

public abstract class Objective {
	
	public double evaluatefunctionperelement(int element)
	{
		return 0;
		
	}
	public LinkedHashMap<Integer, Double> getscores()
	{
		return null;
		
	}
	public int choosebestoption()
	{
		return 0;
		
	}
}
