import java.util.*;

public class main {

	public static Scanner sc;

    public static void main(String[] args) {

    	sc = new Scanner(System.in);

		//HMM1();
		//HMM2();
		//HMM3();
		HMM4();

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

    	Matrix Alpha = Util.compute_alpha(A, B, Pi, M);

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

    	Matrix Alpha = Util.compute_alpha(A, B, Pi, M);

    	
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