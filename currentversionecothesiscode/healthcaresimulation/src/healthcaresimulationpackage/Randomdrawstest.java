package healthcaresimulationpackage;

import java.util.Random;

public class Randomdrawstest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	Randomdraws testrandomdraw=new Randomdraws(0,0,"u",12);
	for(int i=0;i<10000;i++)
	{
		if(testrandomdraw.draw()!=0)
		{
			System.out.println("this does not work");
		}
	}
	
	}

}
