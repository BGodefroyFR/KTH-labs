public class RandomValues {
	
	public static void main(String[] args)
	{
		int M = Integer.parseInt(args[0]);
		int N = Integer.parseInt(args[1]);

		String res = M + " " + N + " ";

		for(int j = 0; j < M; j++)
		{
			double[] rand = new double[N];
			double sum = 0;

			for(int i = 0; i < N; i++)
			{
				rand[i] = Math.random();
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