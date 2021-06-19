package healthcaresimulationpackage;

public class Location {

	private double xcoordinate;
	private double ycoordinate;
	public Location (double coordinatex, double coordinatey)
	{
		xcoordinate=coordinatex;
		ycoordinate=coordinatey;
	}
	
	public Double getxcoordinate()
	{
		return xcoordinate;
	}
	public Double getycoordinate()
	{
		return ycoordinate;
	}
	public Location deepcopy()
	{
		Location copy=new Location(xcoordinate,ycoordinate);
		return copy;
	}
	/**
	 * dist is always calculated one dimensionally
	 * @param otherloc
	 * @return
	 */
	public Double calcdist(Location otherloc)
	{
		double totaldist=0;
		if(otherloc.getxcoordinate()>xcoordinate)
		{
			totaldist=totaldist+otherloc.getxcoordinate()-xcoordinate;
		}
		else
		{
			totaldist=totaldist-otherloc.getxcoordinate()+xcoordinate;
		}
		if(otherloc.getycoordinate()>ycoordinate)
		{
			totaldist=totaldist+otherloc.getycoordinate()-ycoordinate;
		}
		else
		{
			totaldist=totaldist-otherloc.getycoordinate()+ycoordinate;
		}
		return totaldist;
	}
}
