import java.util.*;

public class main {

	public static Scanner sc;

    public static void main(String[] args) {

    	sc = new Scanner(System.in);

		//HMM1();
		//HMM2();
		//HMM3();
		HMM4_2();

		sc.close();
    }

    public static void HMM1() {

    	List<Double> valA = readLine();
    	List<Double> valB = readLine();
    	List<Double> valPi = readLine();

    	Matrix A = new Matrix(valA);
    	Matrix B = new Matrix(valB);
    	Matrix Pi = new Matrix(valPi);

    	Matrix res = new Matrix(Pi.times(A).times(B));
    	res.output();
    }

    public static void HMM2() {

        List<Double> valA = readLine();
        List<Double> valB = readLine();
        List<Double> valPi = readLine();
        List<Double> valM = readLine();

        Matrix A = new Matrix(valA);
        Matrix B = new Matrix(valB);
        Matrix Pi = new Matrix(valPi);

        valM.add(0, 1.0);
        Matrix M = new Matrix(valM);

        Matrix Alpha = new Matrix(valM.get(1).intValue(), A.getColumnNum());

        for(int i = 1; i <= A.getColumnNum(); i++)
        {
            Alpha.set(1, i, Pi.get(1, i) * B.get(i, (int)M.get(1, 1) + 1));
        }

        for(int t = 2; t <= valM.get(1).intValue(); t++)
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

        double res = 0;
        for(int j = 1; j <= A.getColumnNum(); j++)
        {
            res += Alpha.get(M.getColumnNum(), j);
        }

        System.out.println(res);
    }
    
    public static void HMM3() {

    	List<Double> valA = readLine();
    	List<Double> valB = readLine();
    	List<Double> valPi = readLine();
    	List<Double> valM = readLine();

    	Matrix A = new Matrix(valA);
    	Matrix B = new Matrix(valB);
    	Matrix Pi = new Matrix(valPi);

    	valM.add(0, 1.0);
    	Matrix M = new Matrix(valM);

    	Matrix Theta = new Matrix(valM.get(1).intValue(), A.getColumnNum());
    	Matrix Theta_idx = new Matrix(M.getColumnNum(), A.getColumnNum());

    	for(int i = 1; i <= A.getRowNum(); i++)
    	{
    		Theta.set(1, i, B.get(i, (int)M.get(1, 1) + 1) * Pi.get(1, i));
    		Theta_idx.set(1, i, i-1);
    	}

    	for(int t = 2; t <= valM.get(1).intValue(); t++)
	    {
	    	for(int i = 1; i <= A.getColumnNum(); i++)
	    	{
	    		double max = 0;
	    		int bestIdx = -1;

	    		for(int j = 1; j <= A.getColumnNum(); j++)
		    	{
		    		double tmp2 = A.get(j, i) * Theta.get(t-1, j) * B.get(i, (int)M.get(1, t) + 1);

					if(tmp2 >= max)
					{
						max = tmp2;
						bestIdx = j;
					}
		    	}
		    	Theta.set(t, i, max);
		    	Theta_idx.set(t, i, bestIdx-1);
		    }
    	}

    	double maxProb = 0;
    	int bestIdx = -1;
    	for(int i = 1; i <= Theta.getColumnNum(); i++)
    	{
    		if(Theta.get(Theta.getRowNum(), i) >= maxProb)
    		{
    			maxProb = Theta.get(Theta.getRowNum(), i);
    			bestIdx = i;
    		}
    	}

    	String res = "";
    	int idx = bestIdx-1;

    	res = (idx) + " ";

    	for(int i = Theta_idx.getRowNum(); i >= 2; i--)
    	{
    		idx = (int)Theta_idx.get(i, idx+1);
    		res = (idx) + " " + res;
    	}

    	System.out.println(res);
    }

    public static void HMM4() {

    	List<Double> valA = readLine();
    	List<Double> valB = readLine();
    	List<Double> valPi = readLine();
    	List<Double> valM = readLine();

    	Matrix A = new Matrix(valA);
    	Matrix B = new Matrix(valB);
    	Matrix Pi = new Matrix(valPi);

    	valM.add(0, 1.0);
    	Matrix M = new Matrix(valM);
        double dist = 1;
        double diff;
        double last_obs_prob = 0;

        Matrix new_A = A;
        Matrix new_B = B;
        Matrix new_Pi = Pi;

        do {

        	A = new_A;
            B = new_B; 
            Pi = new_Pi;

            Matrix Alpha = Util.compute_alpha(A, B, Pi, M);
            Matrix Beta = Util.compute_beta(A, B, Pi, M);
            double[][][] Digamma = Util.compute_digamma(A, B, Pi, M, Alpha, Beta);
            Matrix Gamma = Util.compute_gamma(Digamma);

            new_A = Util.recompute_A(Digamma, Gamma);
            new_B = Util.recompute_B(M, Gamma);
            new_Pi = Util.recompute_Pi(Gamma);

            double obs_prob = Util.compute_obs_prob(Alpha);
            System.out.println(obs_prob);
            diff = obs_prob - last_obs_prob;
            last_obs_prob = obs_prob;

        } while (diff > 0);

        A.output2();
        B.output2();
    }

    public static List<Double> readLine()
    {
    	String input = sc.nextLine();
    	List<Double> res = new ArrayList<Double>();

    	int lastIndex = -1;
    	while(true)
    	{
    		int index = input.indexOf(" ", lastIndex+1);
    		
    		if(index == -1)
    		{
    			if(lastIndex+1 < input.length()) {
					res.add(Double.parseDouble(input.substring(lastIndex+1)));
				}
    			break;
    		}

    		res.add(Double.parseDouble(input.substring(lastIndex+1, index)));
    		lastIndex = index;

    		while(input.indexOf(" ", lastIndex+1) == lastIndex+1)
    		{
    			lastIndex ++;
    		}
    	}
    	return res;
    }

    public static void HMM4_2()
	{
		List<Double> valA = readLine();
    	List<Double> valB = readLine();
    	List<Double> valPi = readLine();
    	List<Double> valM = readLine();

    	double[][] A = Util.createArray2D(valA);
    	double[][] B = Util.createArray2D(valB);
    	double[] Pi = Util.createArray1D(valPi);

    	valM.add(0, 1.0);
    	double[] O = Util.createArray1D(valM);

        int maxIters = 100000;
        int iters = 0;
        double logProb = -1e300;
        double oldLogProb;

        double[][] newA = A;
    	double[][] newB = B;

        int N = A.length;
        int M = B[0].length;
        int T = O.length;

        do {
        	A = Util.getArrayCopy(newA);
        	B = Util.getArrayCopy(newB);

        	oldLogProb = logProb;

            double[] c = new double[T];
            double[][] Alpha = new double[T][N];
            double[][] Beta = new double[T][N];
            double[][][] DiGamma = new double[T][N][N];
            double[][] Gamma = new double[T][N];

            // compute Alpha_0(i)
            c[0] = 0;
            for(int i = 0; i <= N - 1; i++)
            {
            	Alpha[0][i] = Pi[i] * B[i][(int)O[0]];
            	c[0] = c[0] + Alpha[0][i];
            }

            // scale the Alpha_0(i)
            c[0] = 1.0 / c[0];
            for(int i = 0; i <= N - 1; i++)
            	Alpha[0][i] = c[0] * Alpha[0][i];

            // compute Alpha_t(i)
            for(int t = 1; t <= T - 1; t++)
            {
            	c[t] = 0;
            	for(int i = 0; i <= N - 1; i++)
            	{
            		Alpha[t][i] = 0;
            		for(int j = 0; j <= N - 1; j++)
            		{
            			Alpha[t][i] = Alpha[t][i] + Alpha[t-1][j] * A[j][i];
            		}
            		Alpha[t][i] = Alpha[t][i] * B[i][(int)O[t]];
            		c[t] = c[t] + Alpha[t][i];
            	}

            	// scale Alpha_t(i)
            	c[t] = 1.0 / c[t];
            	for(int i = 0; i <= N-1; i++)
            		Alpha[t][i] = c[t] * Alpha[t][i];
            }

            for(int i = 0; i <= N-1; i++)
            	Beta[T-1][i] = c[T-1];

            // The Beta-pass
            for(int t = T-2; t >= 0; t--)
            {
            	for(int i = 0; i <= N-1; i++)
            	{
            		Beta[t][i] = 0;
            		for(int j = 0; j <= N-1; j++)
            			Beta[t][i] = Beta[t][i] + A[i][j] * B[j][(int)O[t+1]] * Beta[t+1][j];
            	
            		// scale
            		Beta[t][i] = c[t] * Beta[t][i];
            	}
            }

            // Compute Gamma and T-gamma
            for(int t = 0; t <= T-2; t++)
            {
            	double denom = 0.0;
            	for(int i = 0; i <= N-1; i++)
            	{
            		for(int j = 0; j <= N-1; j++)
            		{
            			denom = denom + Alpha[t][i] * A[i][j] * B[j][(int)O[t+1]] * Beta[t+1][j];
            		}
            	}
            	for(int i = 0; i <= N-1; i++)
            	{
            		Gamma[t][i] = 0;
            		for(int j = 0; j <= N-1; j++)
            		{
            			DiGamma[t][i][j] = (Alpha[t][i] * A[i][j] * B[j][(int)O[t+1]] * Beta[t+1][j]) / denom;
            			Gamma[t][i] = Gamma[t][i] + DiGamma[t][i][j];
            		}
            	}
            }

            // special case
            double denom = 0;
            for(int i = 0; i <= N-1; i++)
            	denom = denom + Alpha[T-1][i];
            for(int i = 0; i <= N-1; i++)
            	Gamma[T-1][i] = Alpha[T-1][i] / denom;

            // Re-estimate A, B and Pi
            for(int i = 0; i <= N-1; i++)
            	Pi[i] = Gamma[0][i];

            for(int i = 0; i <= N-1; i++)
            {
            	for(int j = 0; j <= N-1; j++)
            	{
            		double numer = 0.0;
            		denom = 0.0;
            		for(int t = 0; t <= T-2; t++)
            		{
            			numer = numer + DiGamma[t][i][j];
            			denom = denom + Gamma[t][i];
            		}
            		newA[i][j] = numer / denom;
            	}
            }

            for(int i = 0; i <= N-1; i++)
            {
            	for(int j = 0; j <= M-1; j++)
            	{
            		double numer = 0.0;
            		denom = 0.0;

            		for(int t = 0; t <= T-1; t++)
            		{
            			if(O[t] == j)
            				numer = numer + Gamma[t][i];
            			denom = denom + Gamma[t][i];
            		}
            		newB[i][j] = numer / denom;
            	}
            }

            // Compute log
            logProb = 0.0;
            for(int i = 0; i <= T-1; i++)
            	logProb = logProb + Math.log(c[i]);
            logProb = (-1) * logProb;

            iters ++;

        } while(iters < maxIters && logProb > oldLogProb);

        System.out.println("iterations: " + iters);

        List<Double> val_solA = readLine();
    	List<Double> val_solB = readLine();
    	List<Double> val_solPi = readLine();

    	double[][] sol_A = Util.createArray2D(val_solA);
    	double[][] sol_B = Util.createArray2D(val_solB);
    	double[] sol_Pi = Util.createArray1D(val_solPi);

        System.out.println("distance A: " + Util.compute_HMM_distance2D(A, sol_A));
        System.out.println("distance B: " + Util.compute_HMM_distance2D(B, sol_B));
        System.out.println("distance Pi: " + Util.compute_HMM_distance1D(Pi, sol_Pi));
        System.out.println();

        Util.output_array(A);
        Util.output_array(B);
        Util.output_array1D(Pi);
	}
}