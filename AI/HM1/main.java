import java.util.*;

public class main {

	public static Scanner sc;

    public static void main(String[] args) {

    	sc = new Scanner(System.in);

		//HMM1();
		HMM2();

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