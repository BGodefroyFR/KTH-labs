public class Util
{

	public static Matrix compute_alpha(Matrix A, Matrix B, Matrix Pi, Matrix M)
	{
		Matrix Alpha = new Matrix(M.getColumnNum(), A.getColumnNum());

		for(int i = 1; i <= A.getColumnNum(); i++)
		{
			Alpha.set(1, i, Pi.get(1, i) * B.get(i, (int)M.get(1, 1) + 1));
		}

		for(int t = 2; t <= M.getColumnNum(); t++)
		{
	    	for(int i = 1; i <= A.getColumnNum(); i++)
	    	{
	    		double tmp = 0;
		    	for(int j = 1; j <= A.getColumnNum(); j++)
		    	{
		    		tmp += A.get(j, i) * Alpha.get(t-1, j);
		    	}

		    	Alpha.set(t, i, tmp * B.get(i, (int)M.get(1, t) + 1));
		    }
		}

		return Alpha;
	}

}