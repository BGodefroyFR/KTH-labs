public class RandomValues_gaussian {
	
	public static void main(String[] args)
	{
		int M = Integer.parseInt(args[0]);
		int N = Integer.parseInt(args[1]);
		double maxDev = Double.parseDouble(args[2]);

		String res = M + " " + N + " ";

		for(int j = 0; j < M; j++)
		{
			double[] rand = new double[N];
			double sum = 0;

			for(int i = 0; i < N; i++)
			{
				rand[i] = 1.0 + maxDev * Math.random();
				sum += rand[i];
			}

			for(int i = 0; i < N; i++)
			{
				res += (rand[i] / sum) + " ";
			}
		}

		System.out.println(res);
	}
}