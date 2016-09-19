import java.util.*;

class Player {

	private final int nbHiddenState = 3;
	private final int nbObs = 9;
	
    private final int START_SHOOTING_ROUND = 1;
    private final int START_SHOOTING_STEP = 80;

	private final int COMPUTE_DEADLINE = 1900; // ms
	private final int START_MODEL = 80;
	private final int START_SHOOTING = 92;
	private final double MIN_PROB_SHOOTING = 0.75;

	private final double PROTECTED_BIRD_PROB = 0.03;
    public static final double PROTECTED_SHOOT_MALUS = 40;

	private final double MAX_PROT_BIRD_PROB_SHOOT = 0.05;

	private int stepCounter;
	private int roundCounter;
	private MyBird[] currentBirds;
	private Species[] species;
    private List<MyBird> previousBirds;

	private int nbPreviousBirds;

    public Player() {

    	stepCounter = 0;
    	roundCounter = 0;
    	nbPreviousBirds = 0;
        previousBirds = new ArrayList<MyBird>();

    	species = new Species[6];
    	for(int i = 0; i < 6; i++)
    		species[i] = new Species();
    }

    public Action shoot(GameState pState, Deadline pDue) {
        
        // Init
        if(pState.getRound() > roundCounter || stepCounter == 0) // new round
        {
        	roundCounter = pState.getRound();
        	stepCounter = 0;

        	currentBirds = new MyBird[pState.getNumBirds()];
        	for(int i = 0; i < currentBirds.length; i++)
        	{
        		currentBirds[i] = new MyBird(nbHiddenState, nbObs);
        	}
        }

        // Collect observations
        for(int i = 0; i < currentBirds.length; i++)
        {
        	currentBirds[i].addObservation(stepCounter, pState.getBird(i).getLastObservation());
        }

        double bestEsperance = -1e300;
        int bestBird = 0;
        int nextShoot = 0;
        Action a = cDontShoot;

        if(roundCounter >= START_SHOOTING_ROUND && stepCounter >= START_SHOOTING_STEP)
        {
            for(int i = 0; i < currentBirds.length; i++)
            {
                if(pState.getBird(i).isAlive())
                {
                    MyBird.guessSpecies(currentBirds[i], species, previousBirds, false);
                    currentBirds[i].computeNextObservation(species);
                    currentBirds[i].computeShootGainEsperance(species);
                    //System.err.println(currentBirds[i].shootGainEsperance);
                    if(currentBirds[i].shootGainEsperance > 0.0 && currentBirds[i].shootGainEsperance > bestEsperance)
                    {
                        bestEsperance = currentBirds[i].shootGainEsperance;
                        nextShoot = currentBirds[i].nextPosition.index;
                        bestBird = i;
                    }
                }
            }
            //System.err.println("Shoot: " + bestBird + " - " + nextShoot + " - " + bestEsperance);
            if(bestEsperance > 0.0)
            {
                //System.err.println("Shoot: " + bestBird + " - " + nextShoot + " - " + bestEsperance);
                a = new Action(bestBird, nextShoot);
            }
        }

        if(roundCounter > 0 && stepCounter < 6)
        {
            if(! species[stepCounter].isEmpty)
                species[stepCounter].updateModel(roundCounter);
        }

        stepCounter ++;
        return a;
    }

    public int[] guess(GameState pState, Deadline pDue) {

        int[] lGuess = new int[pState.getNumBirds()];

        for(int i = 0; i < currentBirds.length; i++)
    	{
    		MyBird.guessSpecies(currentBirds[i], species, previousBirds, true);
        	lGuess[i] = currentBirds[i].guessedSpecies;
        }

        //for (int i = 0; i < pState.getNumBirds(); ++i)
        //	lGuess[i] = Constants.SPECIES_UNKNOWN;
        return lGuess;
    }

    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {

    	for(int i = 0; i < currentBirds.length; i++)
    	{
    		currentBirds[i].guessedSpecies = pSpecies[i];
    		//System.err.println("reveal: " + pSpecies[i]);
    	}

        for(int i = 0; i < currentBirds.length; i++)
        {
            previousBirds.add(currentBirds[i]);
            species[currentBirds[i].guessedSpecies].addBird(currentBirds[i]);
            nbPreviousBirds ++;
        }
        for(int i = 0; i < species.length; i++)
            species[i].computeMoveMatrix();

    	//computeSpeciesProtectedProb();
    }

    private void computeSpeciesProtectedProb()
    {
    	for(int i = 0; i < 6; i++)
			species[i].protectedProb = Util.KparmiN(species[i].nbBirds, nbPreviousBirds)
    			* Math.pow(PROTECTED_BIRD_PROB, species[i].nbBirds) * Math.pow(1.0 - PROTECTED_BIRD_PROB, nbPreviousBirds - species[i].nbBirds);
    }

    public static final Action cDontShoot = new Action(-1, -1);
}
