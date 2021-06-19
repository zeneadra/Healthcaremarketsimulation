package healthcaresimulationpackage;

public abstract class Localizedagent extends Restrictableactor {
	
	protected Location loc;
	public Localizedagent(Location locin, Government gov)
	{
		super(gov);
		loc=locin;
	}
	
	public Location getloccopy()
	{
		return loc.deepcopy();
		
	}
	public Location getloc()
	{
		return loc;
	}
	public double getdistance(Localizedagent otheragent)
	{
		return loc.calcdist(otheragent.getloc());
	}
}
