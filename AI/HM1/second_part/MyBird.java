import java.util.*;

public class MyBird
{
	public final static double CLUSTERING_MIN_FACTOR = 2.0;
	public final static double MAX_DISTANCE_SPECIES_CONFIDENCE = 1.4;
	public final static double NEW_SPECIES_GUESS_MALUS = 0.3;

	public final static double MAX_PROTECTED_PROB = 1.0;

	public Model model;
	public int[] obs;
	public State nextPosition;
	public double shootGainEsperance;

	public int guessedSpecies;
	public double guessProb;

	public int[] observations;
	public double[] moveMatrix;

	public int nbObs;

	public MyBird(int nbHiddenState, int nbObs)
	{
		model = new Model(nbHiddenState, nbObs);
		model.randomize();
		obs = new int[99];

		nbObs = 0;
		shootGainEsperance = 0;

		observations = new int[10];
		moveMatrix = new double[9];

		guessedSpecies = 0;
		guessProb = 0.0;

		for(int i = 0; i < observations.length; i++)
		{
			observations[i] = 0;
		}
		for(int i = 0; i < moveMatrix.length; i++)
		{
			moveMatrix[i] = 0;
		}
	}

	public void addObservation(int step, int obs)
	{
		this.obs[step] = obs;
		if(this.obs[step] >= 0)
		{
			observations[this.obs[step]] ++;
			observations[9] ++;
			nbObs++;
		}
	}

	public void computeMoveMatrix()
	{
		for(int i = 0; i < moveMatrix.length; i++)
		{
			moveMatrix[i] = ((double)observations[i]) / (observations[9] == 0 ? 1.0 : (double)observations[9]);
		}
	}

	public static void guessSpecies(MyBird mybird, Species[] species, List<MyBird> previousBirds, boolean tryNewSpecies)
	{
		mybird.computeMoveMatrix();

		double bestDist = 1e300;
		mybird.guessedSpecies = 0;

		boolean[] existSpecies = new boolean[6];

		for(int i = 0; i < previousBirds.size(); i++)
		{
			double dist = Util.compute_HMM_distance1D(mybird.moveMatrix, previousBirds.get(i).moveMatrix);
			existSpecies[previousBirds.get(i).guessedSpecies] = true;

			if(dist < bestDist)
			{
				bestDist = dist;
				mybird.guessedSpecies = previousBirds.get(i).guessedSpecies;
				mybird.guessProb = getSpeciesProb(dist);
			}
		}

		if(mybird.guessProb < 0.5)
		{
			int nbNewSpecies = 0;
			int newSpeciesIdx = -1;
			for(int i = 0; i < 6; i++)
			{
				if(!existSpecies[i])
				{
					nbNewSpecies ++;
					if(newSpeciesIdx == -1)
						newSpeciesIdx = i;
				}
			}
			if(nbNewSpecies > 0 && tryNewSpecies)
			{
				double newSpeciesProb = (1.0 - mybird.guessProb) / nbNewSpecies * NEW_SPECIES_GUESS_MALUS;
				if(newSpeciesProb > mybird.guessProb)
				{
					mybird.guessProb = newSpeciesProb;
					mybird.guessedSpecies = newSpeciesIdx;
				}
			}
		}

		//System.err.println("guess: " + bestDist + " - " + mybird.guessProb);
	}

	public void estimateModel(int step)
	{
		//model.randomize();
		//model = HMM.estimateModel(model, obs, step);
	}

	public void computeNextPosition(int step)
	{
		nextPosition = HMM.getMostLikelyLastHiddenState(model, Util.createMatrixFromArray1D(obs, step));
		/*if(show) {
			System.err.println(nextPosition.index + " - " + nextPosition.prob);
			model.show();
			for(int i = 0; i < obs.length && i<= step; i++)
				System.err.println(obs[i]);
		}*/
	}

	private static double getSpeciesProb(double dist)
	{
		double prob = 405.2*Math.pow(dist, 4)
						- 184.88*Math.pow(dist, 3)
						+ 9.7528*Math.pow(dist, 2)
						- 0.4933*dist
						+ 1.0045;
		                           

		if(prob < 0.0)
			prob = 0.0;
		else if(prob > 1.0)
			prob = 1.0;

		return prob;
	}

	public void computeNextObservation(Species[] species)
	{
		nextPosition = HMM.computeMostProbableNextObservation(species[guessedSpecies].model, obs, nbObs);
	}

	public void computeShootGainEsperance(Species[] species)
	{
		shootGainEsperance = 2 * nextPosition.prob * guessProb - 1.0;
		if(species[guessedSpecies].protectedProb > MAX_PROTECTED_PROB)
			shootGainEsperance = -1;
	}
}