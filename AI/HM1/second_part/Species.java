import java.util.*;

public class Species
{
	public static int NB_TEST_OBS = 5;

	public int[] observations;
	public double[] moveMatrix;
	public double protectedProb;

	public List<Integer> obs;

	public int nbBirds;
	public double isProtectedProb;
	public boolean isEmpty;

	public double modelBestScore;
	public boolean isModelChoiceFinished;

	public int nbHiddenStates;

	public Model model;

	public Species()
	{
		observations = new int[10];
		moveMatrix = new double[9];

		nbHiddenStates = 1;
		modelBestScore = 0;
		resetModel();

		obs = new ArrayList<Integer>();

		isEmpty = true;
		nbBirds = 0;
		isProtectedProb = 1.0;

		protectedProb = 1.0;

		for(int i = 0; i < observations.length; i++)
		{
			observations[i] = 0;
		}
		for(int i = 0; i < moveMatrix.length; i++)
		{
			moveMatrix[i] = 0;
		}
	}

	public void addBird(MyBird mybird)
	{
		isEmpty = false;
		nbBirds ++;
		for(int i = 0; i < mybird.obs.length; i++)
		{
			if(mybird.obs[i] < 0)
				break;
			obs.add(mybird.obs[i]);
			observations[mybird.obs[i]] ++;
			observations[9] ++;
		}
	}

	public void computeMoveMatrix()
	{
		for(int i = 0; i < moveMatrix.length; i++)
		{
			moveMatrix[i] = ((double)observations[i]) / (observations[9] == 0 ? 1.0 : (double)observations[9]);
		}
	}

	public double getDistance(MyBird mybird)
	{
		return Util.compute_HMM_distance1D(mybird.moveMatrix, this.moveMatrix);
	}

	public void resetModel()
	{
		isModelChoiceFinished = false;
	}

	public void updateModel(int round)
	{
		Model newModel;

		Matrix lastObs = new Matrix(1, NB_TEST_OBS);
		for(int i = 1; i <= NB_TEST_OBS; i ++)
			lastObs.set(1, i, obs.get(obs.size() - NB_TEST_OBS - 1 + i));

		newModel = new Model(nbHiddenStates, 9);
		newModel.randomize();
		newModel = HMM.estimateModel(newModel, obs, obs.size() - NB_TEST_OBS);

		double score = HMM.getObsSeqProb(newModel, lastObs);

		if(score > modelBestScore)
		{
			nbHiddenStates ++;
			modelBestScore = score;
			model = newModel;
		}
		else
		{
			isModelChoiceFinished = true;
			System.err.println("Nb hidden states: " + nbHiddenStates);
		}
	}
}