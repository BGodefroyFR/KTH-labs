public class Util
{

	public static Matrix compute_alpha(Matrix A, Matrix B, Matrix Pi, Matrix M)
	{
		Matrix Alpha = new Matrix(M.getColumnNum(), A.getColumnNum());

		for(int i = 1; i <= A.getColumnNum(); i++)
		{
			Alpha.set(1, i, Pi.get(1, i) * B.get(i, (int)M.get(1, 1) + 1) * 1e300);
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

	public static Matrix compute_beta(Matrix A, Matrix B, Matrix Pi, Matrix M)
	{
		Matrix Beta = new Matrix(M.getColumnNum(), A.getColumnNum());

    	for (int i = 1; i <= A.getColumnNum(); i++)
		{
			Beta.set(M.getColumnNum(), i, 1 * 1e300);

		}

		for (int t = M.getColumnNum() - 1; t >= 1; t--)
		{
			for (int i = 1; i <= A.getColumnNum(); i++)
			{
				double temp1 = 0;

				for (int j = 1; j <= A.getColumnNum(); j++)
				{
					temp1 += Beta.get(t+1, j) * B.get(j, (int)M.get(1, t) + 1) * A.get(i, j);
				}

				Beta.set(t, i, temp1);
			}
		}

		return Beta;
	}

	public static double[][][] compute_digamma(Matrix A, Matrix B, Matrix Pi, Matrix M, Matrix Alpha, Matrix Beta)
	{	

		double[][][] Digamma = new double[M.getColumnNum() - 1][A.getColumnNum()][A.getColumnNum()];
		double alphaTSum = 0;

		//Alpha.show();
		//System.out.print(Alpha.getRowNum() + " " + Alpha.getColumnNum());
		//Beta.show();
		//System.out.println();
		for (int k = 1; k <= A.getColumnNum(); k++)
		{
			alphaTSum += Alpha.get(Alpha.getRowNum(), k);
			//System.out.print(alphaTSum + " ");
		}
		//System.out.println();

		for (int t = 1; t <= M.getColumnNum()-1; t++)
		{
			for (int i = 1; i <= A.getColumnNum(); i++)
			{

				for (int j = 1; j <= A.getColumnNum(); j++)
				{
					Digamma[t-1][i-1][j-1] = ((Alpha.get(t, i) * A.get(i, j) * B.get(j, (int)M.get(1, t + 1) + 1) * Beta.get(t+1, j)))/(alphaTSum)/1e300;
					//System.out.println("Alpha: " + Alpha.get(t, i) + " A: " + A.get(i, j) + " B: " + B.get(j, (int)M.get(1, t + 1) + 1) + " Beta: " + Beta.get(t+1, j));
				}
				//System.out.println();

			}
		}
		return Digamma;

	}

	public static Matrix compute_gamma(double[][][] Digamma)
	{
		Matrix Gamma = new Matrix(Digamma.length, Digamma[0].length);

		for (int t = 1; t <= Digamma.length; t++)
		{
			for (int i = 1; i <= Digamma[0].length; i++)
			{
				double temp = 0;
				for (int j = 1; j <= Digamma[0].length; j++)
				{
					temp += Digamma[t-1][i-1][j-1];
				}

				Gamma.set(t, i, temp);
			}
		}

		return Gamma;
	}

	public static Matrix recompute_A(double[][][] Digamma, Matrix Gamma)
	{
		Matrix A = new Matrix(Gamma.getColumnNum(), Gamma.getColumnNum());

		for (int i = 1; i <= Gamma.getColumnNum(); i++)
		{
			for (int j = 1; j <= Gamma.getColumnNum(); j++)
			{
				double tempGamma = 0;
				double tempDigamma = 0;
				for (int t = 1; t <= Gamma.getRowNum(); t++)
				{
					tempDigamma += Digamma[t-1][i-1][j-1];
					tempGamma += Gamma.get(t, i);
				}

				A.set(i, j, tempDigamma/tempGamma);
			}
		}
		return A;
	}

	public static Matrix recompute_B(Matrix M, Matrix Gamma)
	{
		Matrix B = new Matrix(Gamma.getColumnNum(), Gamma.getColumnNum());

		for (int k = 1; k <= Gamma.getColumnNum(); k++)
		{
			for (int j = 1; j <= Gamma.getColumnNum(); j++)
			{
				double tempGamma1 = 0;
				double tempGamma2 = 0;

				for (int t = 1; t <= Gamma.getRowNum(); t++)
				{
					tempGamma1 += One(M, t, k) * Gamma.get(t, j);
					tempGamma2 += Gamma.get(t, j);
				}

				B.set(j, k, tempGamma1/tempGamma2);
			}
		}
		return B;
	}

	public static double One(Matrix M, int t, int k)
	{
		boolean res = (k == M.get(1, t)+1);
		return res ? 1.0 : 0.0;
	}

	public static Matrix recompute_Pi(Matrix Gamma)
	{
		Matrix Pi = new Matrix(1, Gamma.getColumnNum());

		for (int i = 1; i <= Gamma.getColumnNum(); i++)
		{
			Pi.set(1, i, Gamma.get(1, i));
		}

		return Pi;
	}

	public static double compute_difference(Matrix M1, Matrix M2)
	{
		double diff = 0;

		for (int i = 1; i <= M1.getRowNum(); i++)
		{
			for (int j = 1; j <= M1.getColumnNum(); j++)
			{
				diff += Math.abs(M1.get(i, j) - M2.get(i, j));
			}
		}

		return diff/(M1.getRowNum() * M1.getColumnNum());
	}

	public static void show_array(double[][][] array)
	{
		for (int t = 0; t < array.length; t++)
		{
			for (int i = 0; i < array[t].length; i++)
			{
				for (int j = 0; j < array[t][i].length; j++)
				{
					System.out.print(array[t][i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
}