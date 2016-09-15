public class Model
{
	public Matrix A;
	public Matrix B;
	public Matrix Pi;

	public Model()
	{}

	public Model(Matrix A, Matrix B, Matrix Pi)
	{
		this.A = A;
		this.B = B;
		this.Pi = Pi;
	}

	public Model(double[][] arr_A, double[][] arr_B, double[] arr_Pi)
	{
		A = new Matrix(arr_A.length, arr_A[0].length);
		B = new Matrix(arr_B.length, arr_B[0].length);
		Pi = new Matrix(1, arr_Pi.length);

		for(int i = 0; i < arr_A.length; i++)
		{
			for(int j = 0; j < arr_A[0].length; j++)
			{
				this.A.set(i+1, j+1, arr_A[i][j]);
			}
		}
		for(int i = 0; i < arr_B.length; i++)
		{
			for(int j = 0; j < arr_B[0].length; j++)
			{
				this.B.set(i+1, j+1, arr_B[i][j]);
			}
		}
		for(int i = 0; i < arr_Pi.length; i++)
		{
			this.Pi.set(1, i+1, arr_Pi[i]);
		}
	}
}