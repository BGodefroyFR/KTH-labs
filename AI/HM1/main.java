import java.util.*;

public class main {

	public static Scanner sc;

    public static void main(String[] args) {

    	sc = new Scanner(System.in);

		//HMM1();
		//HMM2();
		HMM3();

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

    public static void HMM3_2() {

    	List<Double> valA = readLine();
    	List<Double> valB = readLine();
    	List<Double> valPi = readLine();
    	List<Double> valM = readLine();

    	Matrix A = new Matrix(valA);
    	Matrix B = new Matrix(valB);
    	Matrix Pi = new Matrix(valPi);

    	valM.add(0, 1.0);
    	Matrix M = new Matrix(valM);

    	int[] res = new int[M.getColumnNum()];

    	Matrix Alpha = new Matrix(valM.get(1).intValue(), A.getColumnNum());

    	double max = 0;
    	for(int i = 1; i <= A.getColumnNum(); i++)
    	{
    		double tmp = Pi.get(1, i) * B.get(i, (int)M.get(1, 1) + 1);
    		Alpha.set(1, i, tmp);

    		if(tmp > max)
    		{
    			max = tmp;
    			res[0] = i;
    		}
    	}

    	for(int t = 2; t <= valM.get(1).intValue(); t++)
    	{
    		double localMax;
    		double globalMax = 0;
    		int globalBestObs = -1;

	    	for(int i = 1; i <= A.getColumnNum(); i++)
	    	{
	    		localMax = 0;
		    	for(int j = 1; j <= A.getColumnNum(); j++)
		    	{
		    		double tmp = A.get(j, i) * Alpha.get(t-1, j);
		    		if(tmp > localMax)
		    		{
		    			localMax = tmp;
		    		}
		    		if(tmp > globalMax)
		    		{
		    			globalMax = tmp;
		    			globalBestObs = i;
		    		}
		    	}

		    	Alpha.set(t, i, localMax * B.get(i, (int)M.get(1, t) + 1));
		    }

		    res[t-1] = globalBestObs;
		}

		String str = "";
		for(int j = 0; j < M.getColumnNum(); j++)
    	{
    		str += res[j] + " ";
    	}

    	System.out.println(str);
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
    	Matrix Theta_idx = new Matrix(1, M.getColumnNum());

    	for(int i = 1; i <= A.getColumnNum(); i++)
    	{
    		Theta.set(1, i, B.get(i, (int)M.get(1, 1) + 1) * Pi.get(1, i));
    	}


    	Theta_idx.set(1, 1, Theta.getBestObservation(1));

    	for(int t = 2; t <= valM.get(1).intValue(); t++)
	    {
	    	for(int i = 1; i <= A.getColumnNum(); i++)
	    	{
	    		double max = 0;

	    		for(int j = 1; j <= A.getColumnNum(); j++)
		    	{
		    		double tmp2 = A.get(j, i) * Theta.get(t-1, j) * B.get(i, (int)M.get(1, t) + 1);

					if(tmp2 > max)
						max = tmp2;
		    	}
		    	Theta.set(t, i, max);
		    	Theta_idx.set(1, t, Theta.getBestObservation(t));
		    }
    	}

    	String res = "";
    	for(int t = 1; t <= valM.get(1).intValue(); t++)
    		res += (int)Theta_idx.get(1,t) + " ";
    	System.out.println(res);
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
}