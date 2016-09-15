import java.util.*;

class Player {

	private final int nbHiddenState = 3;
	private final int nbObs = 9;
	private final int COMPUTE_DEADLINE = 200; // ms
	private final int NB_STATES_START = 10;
	//private final double MIN_SHOOT_PROBABILITY = 0.3;

	private int stepCounter;
	private int roundCounter;
	private MyBird[] mybirds;

    public Player() {
    	stepCounter = 0;
    	roundCounter = 0;
    }

    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     *
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        
        // Init
        if(pState.getRound() > roundCounter || stepCounter == 0) // new round
        {
        	roundCounter = pState.getRound();
        	stepCounter = 0;
        	mybirds = new MyBird[pState.getNumBirds()];
        	for(int i = 0; i < mybirds.length; i++)
        	{
        		mybirds[i] = new MyBird(nbHiddenState, nbObs);
        	}
        }

        // Collect observations
        for(int i = 0; i < mybirds.length; i++)
        {
        	mybirds[i].obs[stepCounter] = pState.getBird(i).getLastObservation();
        }

        System.err.println(roundCounter + " - " + stepCounter);
        int mostPredictibleBirdIdx = 0;
        if(stepCounter >= NB_STATES_START)
        {
        	// Estimate model
	        for(int i = 0; i < mybirds.length; i++)
	        {
	        	if(pState.getBird(i).isAlive())
		        	mybirds[i].estimateModel(stepCounter + 1);
		    }

		    // Predict next birds positions
		    double mostPredictibleBirdProb = 0.0;
		    for(int i = 0; i < mybirds.length; i++)
	        {
	        	if(pState.getBird(i).isAlive())
	        	{
			        mybirds[i].computeNextPosition(stepCounter, i == 1);
			        if(mybirds[i].nextPosition.prob > mostPredictibleBirdProb)
			        {
			        	mostPredictibleBirdProb = mybirds[i].nextPosition.prob;
			        	mostPredictibleBirdIdx = i;
			        }
			   	}
		    }
		}

        Action a;
        if(stepCounter >= NB_STATES_START)
        {
        	a = new Action(mostPredictibleBirdIdx, mybirds[mostPredictibleBirdIdx].nextPosition.index);
        }
        else
        {
        	a = cDontShoot;
        }

        stepCounter ++;

        return a;
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */

        int[] lGuess = new int[pState.getNumBirds()];
        for (int i = 0; i < pState.getNumBirds(); ++i)
            lGuess[i] = Constants.SPECIES_UNKNOWN;
        return lGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    }

    public static final Action cDontShoot = new Action(-1, -1);
}
