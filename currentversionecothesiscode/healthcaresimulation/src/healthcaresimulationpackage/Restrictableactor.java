package healthcaresimulationpackage;

import java.util.ArrayList;

public abstract class Restrictableactor extends Actor {
protected Government gov;
protected boolean restricted;
public Restrictableactor(Government govin)
{
	gov=govin;
}

public ArrayList<Integer> influenceduetorestrict()
{
	ArrayList<Integer> emptylist=new ArrayList<Integer>();
	return emptylist;
}
}
