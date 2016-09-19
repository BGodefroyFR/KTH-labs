public class Model
{
	public Matrix A;
	public Matrix B;
	public Matrix Pi;

	public double convDist;


	public Model()
	{
	}

	public Model(int nbHiddenStates, int nbObs)
	{
		A = new Matrix(nbHiddenStates, nbHiddenStates);
		B = new Matrix(nbHiddenStates, nbObs);
		Pi = new Matrix(1, nbHiddenStates);
	}

	public Model(Matrix A, Matrix B, Matrix Pi)
	{
		this.A = A;
		this.B = B;
		this.Pi = Pi;
	}

	public Model(double[][] arr_A, double[][] arr_B, double[] arr_Pi, double convDist)
	{
		this.convDist = convDist;

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

	public void randomize()
	{
		A.randomize();
		B.randomize();
		Pi.randomize();
	}

	public void show()
	{
		System.err.println("\nA =");
		A.show();
		System.err.println("\nB =");
		B.show();
		System.err.println("\nPi =");
		Pi.show();
	}

	public static Model getCopy(Model initialModel)
	{
		Model newModel = new Model(initialModel.B.getRowNum(), initialModel.B.getColumnNum());
		newModel.A = new Matrix(initialModel.A);
		newModel.B = new Matrix(initialModel.B);
		newModel.Pi = new Matrix(initialModel.Pi);
		newModel.convDist = initialModel.convDist;

		return newModel;
	}

	public double compute_distance(Model other_model)
	{
		return Math.sqrt(Math.pow(Util.compute_HMM_distance(A, other_model.A), 2) + Math.pow(Util.compute_HMM_distance(B, other_model.B), 2));
	}
}