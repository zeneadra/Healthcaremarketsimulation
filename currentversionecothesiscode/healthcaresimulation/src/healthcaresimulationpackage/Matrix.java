package healthcaresimulationpackage;

import java.util.ArrayList;
public class Matrix {
	ArrayList<ArrayList<Double>> M;//first are rows second are columns
	ArrayList<ArrayList<Double>> Mbycols;//same as M but out ArrayList is columns is also equal to transpose of M when interpreted as M

	public Matrix(ArrayList<ArrayList<Double>> matrixinput)
	{
		M=matrixinput;
//		if(M.size()==M.get(0).size()) {
		Mbycols=this.genMbycols();
	//	}
	}
	private ArrayList<ArrayList<Double>> genMbycols() {
		ArrayList<ArrayList<Double>> mtranspose=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<M.get(0).size();i++)
		{
			ArrayList<Double> col=new ArrayList<Double>();
			for(int j=0;j<M.size();j++)
			{
				col.add(M.get(j).get(i));
			}
			mtranspose.add(col);
		}
		return mtranspose;
	}
	public Matrix leftmult(Matrix A)
	{
		return A.rightmult(this);
	}
	public Matrix rightmult(Matrix A)
	{
		ArrayList<ArrayList<Double>> result=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<M.size();i++)
		{
			ArrayList<Double> newrow=new ArrayList<Double>();
			for(int j=0;j<A.getnumcols();j++)
			{

				double entry=this.vectormult(this.getrow(i), A.getcol(j));

				newrow.add(entry);
			}
			result.add(newrow);
		}
		Matrix resultmatrix=new Matrix(result);
		return resultmatrix;
	}
	/**
	 * assumes right dimensions
	 * @param R
	 * @param L
	 * @return
	 */
	public  double vectormult(ArrayList<Double> R, ArrayList<Double> L)
	{
		double total=0;
		for(int i=0;i<R.size();i++)
		{
			total+=R.get(i)*L.get(i);
		}
		return total;
	}
	public ArrayList<Double> getcol(int i)
	{
		if(Mbycols==null)
		{
			Mbycols=this.genMbycols();	
		}
		return Mbycols.get(i);

	}
	public ArrayList<Double> getrow(int i)
	{
		return M.get(i);
	}
	public int getnumcols()
	{
		return M.get(0).size();
	}
	public int getnumrows()
	{
		return M.size();
	}
	public Matrix gettranspose()
	{
		return new Matrix(Mbycols);
	}
	public ArrayList<ArrayList<Double>> getvalues()
	{
		return M;
	}
	public Matrix getsquaredim2inverse()
	{
		double determinant=M.get(0).get(0)*M.get(1).get(1)-M.get(1).get(0)*M.get(0).get(1);
		ArrayList<ArrayList<Double>> newmatrix=new ArrayList<ArrayList<Double>>();
		ArrayList<Double> rowone=new ArrayList<Double>();
		ArrayList<Double> rowtwo=new ArrayList<Double>();
		rowone.add(M.get(1).get(1)/determinant);
		rowone.add(-1*M.get(0).get(1)/determinant);
		newmatrix.add(rowone);
		rowtwo.add(-1*M.get(1).get(0)/determinant);
		rowtwo.add(M.get(0).get(0)/determinant);
		newmatrix.add(rowtwo);
		Matrix inverse=new Matrix(newmatrix);
		return inverse;
	}
	public static ArrayList<Double> WLS(Matrix X, Matrix Y, Matrix W)
	{
		ArrayList<Double> parameters=new ArrayList<Double>();
		X.gettranspose();
		Matrix beta=X.gettranspose().rightmult(W).rightmult(X).getsquaredim2inverse().rightmult(X.gettranspose()).rightmult(W).rightmult(Y);
		parameters=beta.getcol(0);
		return parameters;
		
	}
	/**
	 * construct an outcome matrix from  the second colummn of the inout matrix
	 * @param pricenumpatstime
	 * @return
	 */
	public static Matrix constructWLSXhos( ArrayList<ArrayList<Double>> pricenumpatstime)
	{
		ArrayList<ArrayList<Double>> xdat=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<pricenumpatstime.size();i++)
		{
			ArrayList<Double> row=new ArrayList<Double>();
			row.add((double) 1);
			row.add(pricenumpatstime.get(i).get(0));
			xdat.add(row);
			
		}
		Matrix X=new Matrix(xdat);
		return X;
	}
	/**
	 * construct an outcome matrix from  the second colummn of the inout matrix
	 * @param pricenumpatstime
	 * @return
	 */
	public static Matrix constructWLSYhos(ArrayList<ArrayList<Double>> pricenumpatstime)
	{
		ArrayList<ArrayList<Double>> ydat=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<pricenumpatstime.size();i++)
		{
			ArrayList<Double> row=new ArrayList<Double>();
			row.add(pricenumpatstime.get(i).get(1));
			ydat.add(row);
			
		}
		Matrix Y=new Matrix(ydat);
		return Y;
	}
	/**
	 * construct an outcome matrix from  the third column and the first column 
	 * @param pricenumpatstime
	 * @return
	 */
	public static Matrix constructWLSWhos(ArrayList<ArrayList<Double>> pricenumpatstime, int timeperiod,double considerdprice)
	{
		ArrayList<ArrayList<Double>> wdat=new ArrayList<ArrayList<Double>>();
		long timeperiodssincestart=0;
		for(int i=0;i<pricenumpatstime.size();i++)
		{
			ArrayList<Double> row=new ArrayList<Double>();
			for( int j=0;j<pricenumpatstime.size();j++)
			{
				if(j==i)
				{
					double weightfactor=0;
					if(j==pricenumpatstime.size()-1)
					{
						weightfactor=pricenumpatstime.get(j).get(2);
						weightfactor=weightfactor*((double) timeperiod);
						if(pricenumpatstime.get(j).get(0)>considerdprice)
						{
							weightfactor=weightfactor/((1+pricenumpatstime.get(j).get(0)-considerdprice)*(1+pricenumpatstime.get(j).get(0)-considerdprice));
						}
						else
						{
							weightfactor=weightfactor/((1+considerdprice-pricenumpatstime.get(j).get(0))*(1+considerdprice-pricenumpatstime.get(j).get(0)));

						}
					}
					else
					{
						weightfactor=pricenumpatstime.get(j).get(2);
						weightfactor=weightfactor*(pricenumpatstime.get(j).get(2)+timeperiodssincestart);
						if(pricenumpatstime.get(j).get(0)>considerdprice)
						{
							weightfactor=weightfactor/((1+pricenumpatstime.get(j).get(0)-considerdprice)*(1+pricenumpatstime.get(j).get(0)-considerdprice));
						}
						else
						{
							weightfactor=weightfactor/((1+considerdprice-pricenumpatstime.get(j).get(0))*(1+considerdprice-pricenumpatstime.get(j).get(0)));

						}
					}
					weightfactor=weightfactor/(Math.pow(timeperiod,2));
					timeperiodssincestart=timeperiodssincestart+Math.round(pricenumpatstime.get(j).get(2));
					row.add(weightfactor);
				}
				else
				{
					row.add((double)0);
				}
			}
		wdat.add(row);
		}
		Matrix W=new Matrix(wdat);
		return W;
	}
}
