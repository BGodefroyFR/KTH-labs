import java.util.*;

public class Player {

	private static final double PRUNE_THRESHOLD = 0;

	private static final int SEARCH_DEPTH = 3;

	private static final int INFINITY = (int)2e10;
	private static final int MINUS_INFINITY = (int)(-2e10);

	private static int nbMarksInGame = 0;
	private static double lastScore = MINUS_INFINITY;

    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState gameState, final Deadline deadline) {
    	Vector<GameState> nextStates = new Vector<GameState>();
    	gameState.findPossibleMoves(nextStates);

    	if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
    		return new GameState(gameState, new Move());
    	}

    	if(nbMarksInGame == 0)
    		computeNbMarks(gameState);
    	else
    		nbMarksInGame += 2;

    	int me = (gameState.getNextPlayer() == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X);
    	//evaluate3D(gameState, me, gameState.getNextPlayer());
    	//System.err.println("eval: " + evaluate3D(gameState, me));

    	int searchDepth = SEARCH_DEPTH;

    	//int bonus = random.nextInt(2);
    	//searchDepth += bonus;

    	GameStateEvaluated bestNextState = minimaxPruning(gameState, me, me, searchDepth, searchDepth, (double)MINUS_INFINITY, (double)INFINITY);

    	System.err.println("------ " + bestNextState.eval);

    	/*if(lastScore != MINUS_INFINITY && bestNextState.eval - lastScore < 0)
    	{
    		//System.err.println("try hard !");
    		searchDepth = SEARCH_DEPTH - 1;
    		bestNextState = minimaxPruning(gameState, me, me, searchDepth, searchDepth, (double)MINUS_INFINITY, (double)INFINITY);
    	}*/
    	lastScore = bestNextState.eval;
    	return bestNextState.gameState;
    	//Random random = new Random();
    	//return nextStates.elementAt(random.nextInt(nextStates.size()));
    }

    private GameStateEvaluated minimaxPruning(GameState gameState, int me, int player, int currentSearchDepth, int maxSearchDepth, double alpha, double beta)
    {
    	Vector<GameState> nextGameStates = new Vector<GameState>();
    	gameState.findPossibleMoves(nextGameStates);

    	int other_player = (player == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X);
        int not_me = (me == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X);

    	GameStateEvaluated bestPossible = null;
    	GameStateEvaluated bestNextMove = null;

    	if(nextGameStates.size() == 0 || currentSearchDepth == 0)
    	{
            GameStateEvaluated leaf = new GameStateEvaluated(gameState, computeGamma(gameState, me, not_me, currentSearchDepth));
            //System.err.println(currentSearchDepth + " : " + leaf.eval);
    		return leaf;
    	}
    	else if(player == me)
    	{
    		bestPossible = new GameStateEvaluated(MINUS_INFINITY);

    		for(GameState child: nextGameStates)
    		{
    			GameStateEvaluated v = minimaxPruning(child, me, other_player, currentSearchDepth-1, maxSearchDepth, alpha, beta);

    			if(v.eval > bestPossible.eval)
    			{
    				bestPossible = v;

    				if(bestPossible.eval > alpha)
    					alpha = bestPossible.eval;

    				if(currentSearchDepth == maxSearchDepth)
    				{
    					bestNextMove = new GameStateEvaluated(child, v.eval);
    				}

    				if(beta-PRUNE_THRESHOLD <= alpha) // beta prune
    					break;
    			}
    		}
    	}
    	else // player != me
    	{
    		bestPossible = new GameStateEvaluated(INFINITY);

    		for(GameState child: nextGameStates)
    		{
    			GameStateEvaluated v = minimaxPruning(child, me, other_player, currentSearchDepth-1, maxSearchDepth, alpha, beta);

    			if(v.eval < bestPossible.eval)
    			{
    				bestPossible = v;

    				if(bestPossible.eval < beta)
    					beta = bestPossible.eval;

    				if(currentSearchDepth == maxSearchDepth)
    				{
    					bestNextMove = new GameStateEvaluated(child, v.eval);
    				}

    				if(beta-PRUNE_THRESHOLD <= alpha) // alpha prune
    					break;
    			}
    		}
    	}

    	if(currentSearchDepth == maxSearchDepth)
			return bestNextMove;
		else
			return bestPossible;
    }

    private double computeGamma(GameState gameState, int player, int other_player, int searchDepth)
    {
    	Pair eval = evaluate3D(gameState, player, other_player, searchDepth);

    	double attackFactor = 1.2;

    	/*if(lastScore == MINUS_INFINITY)
    		attackFactor = 0.4;
    	else if(lastScore < -100)
    		attackFactor = 0.1;
    	else if(lastScore < 0)
    		attackFactor = 0.3;
    	else
    		attackFactor = 0.5;
    */
    	//attackFactor = 0.35;
    	double gamma = ((double)eval.player) * attackFactor - ((double)eval.opponent);

    	return gamma;
    }

    private Pair evaluate3D(GameState gameState, int player, int other_player, int searchDepth)
    {
    	Pair eval = new Pair();

    	eval.add(evaluateStraightLines3D(gameState, player, other_player, searchDepth));

    	for(int k = 0; k < 3; k++)
    	{
	    	for(int i = 0; i < GameState.BOARD_SIZE; i++)
	    	{
	    		eval.add(evaluateDiagonals2D(gameState, player, other_player, k, i, searchDepth));
	    	}
	    }

    	eval.add(evaluateDiagonals3D(gameState, player, other_player, searchDepth));

    	return eval;
    }

    private void computeNbMarks(GameState gameState)
    {
    	nbMarksInGame = 0;

    	for(int k = 0; k < GameState.BOARD_SIZE; k++)
    	{
	    	for(int i = 0; i < GameState.BOARD_SIZE; i++)
	    	{
	    		for(int j = 0; j < GameState.BOARD_SIZE; j++)
	    		{
	                int tmp = gameState.at(i, j, k);
	    			if(tmp == Constants.CELL_X || tmp == Constants.CELL_O)
	    				nbMarksInGame ++;
	    		}
	    	}
	    }
    }

    private Pair evaluateStraightLines3D(GameState gameState, int player, int other_player, int searchDepth)
    {
    	Pair eval = new Pair();

    	for(int k = 0; k < GameState.BOARD_SIZE; k++)
    	{
	    	for(int i = 0; i < GameState.BOARD_SIZE; i++)
	    	{
	    		Pair marksWidth = new Pair();
	    		Pair marksHeight = new Pair();
	    		Pair marksDepth = new Pair();
	    		for(int j = 0; j < GameState.BOARD_SIZE; j++)
	    		{
	                // Width
	                int tmp = gameState.at(i, j, k);
	    			if(tmp == player)
	    			{
	    				marksWidth.player ++;
	    				marksWidth.opponent = MINUS_INFINITY;
	    			}
	    			else if(tmp == other_player)
	    			{
	    				marksWidth.opponent ++;
	    				marksWidth.player = MINUS_INFINITY;
	    			}

	                // Height
	                tmp = gameState.at(j, i, k);
	    			if(tmp == player)
	    			{
	    				marksHeight.player ++;
	    				marksHeight.opponent = MINUS_INFINITY;
	    			}
	    			else if(tmp == other_player)
	    			{
	    				marksHeight.opponent ++;
	    				marksHeight.player = MINUS_INFINITY;
	    			}

	    			// Depth
	    			tmp = gameState.at(k, i, j);
	    			if(tmp == player)
	    			{
	    				marksDepth.player ++;
	    				marksDepth.opponent = MINUS_INFINITY;
	    			}
	    			else if(tmp == other_player)
	    			{
	    				marksDepth.opponent ++;
	    				marksDepth.player = MINUS_INFINITY;
	    			}
	    		}

	    		eval.player += getPoints(marksWidth.player, searchDepth) + getPoints(marksHeight.player, searchDepth) + getPoints(marksDepth.player, searchDepth);
	    		eval.opponent += getPoints(marksWidth.opponent, searchDepth) + getPoints(marksHeight.opponent, searchDepth) + getPoints(marksDepth.opponent, searchDepth);
	    	}
	    }

    	return eval;
    }

    private Pair evaluateDiagonals2D(GameState gameState, int player, int other_player, int constantDim, int layer, int searchDepth)
    {
    	Pair eval = new Pair();

		Pair marksDiag1 = new Pair();
		Pair marksDiag2 = new Pair();

		for(int j = 0; j < GameState.BOARD_SIZE; j++)
		{
			int tmp = getGameState(gameState, j, j, constantDim, layer);
			if(tmp == player)
			{
				marksDiag1.player ++;
				marksDiag1.opponent = MINUS_INFINITY;
			}
			else if(tmp == other_player)
			{
				marksDiag1.opponent ++;
				marksDiag1.player = MINUS_INFINITY;
			}

			tmp = getGameState(gameState, j, GameState.BOARD_SIZE - 1 - j, constantDim, layer);
			if(tmp == player)
			{
				marksDiag2.player ++;
				marksDiag2.opponent = MINUS_INFINITY;
			}
			else if(tmp == other_player)
			{
				marksDiag2.opponent ++;
				marksDiag2.player = MINUS_INFINITY;
			}
		}

		eval.player = getPoints(marksDiag1.player, searchDepth) + getPoints(marksDiag2.player, searchDepth);
		eval.opponent = getPoints(marksDiag1.opponent, searchDepth) + getPoints(marksDiag2.opponent, searchDepth);

    	return eval;
    }

    private Pair evaluateDiagonals3D(GameState gameState, int player, int other_player, int searchDepth)
    {
		Pair marksDiag1 = new Pair();
		Pair marksDiag2 = new Pair();
		Pair marksDiag3 = new Pair();
		Pair marksDiag4 = new Pair();

		for(int j = 0; j < GameState.BOARD_SIZE; j++)
		{
			int tmp = gameState.at(j, j, j);
			if(tmp == player)
			{
				marksDiag1.player ++;
				marksDiag1.opponent = MINUS_INFINITY;
			}
			else if(tmp == other_player)
			{
				marksDiag1.opponent ++;
				marksDiag1.player = MINUS_INFINITY;
			}

			tmp = gameState.at(j, j, GameState.BOARD_SIZE - 1 - j);
			if(tmp == player)
			{
				marksDiag2.player ++;
				marksDiag2.opponent = MINUS_INFINITY;
			}
			else if(tmp == other_player)
			{
				marksDiag2.opponent ++;
				marksDiag2.player = MINUS_INFINITY;
			}

			tmp = gameState.at(j, GameState.BOARD_SIZE - 1 - j, j);
			if(tmp == player)
			{
				marksDiag3.player ++;
				marksDiag3.opponent = MINUS_INFINITY;
			}
			else if(tmp == other_player)
			{
				marksDiag3.opponent ++;
				marksDiag3.player = MINUS_INFINITY;
			}

			tmp = gameState.at(j, GameState.BOARD_SIZE - 1 - j, GameState.BOARD_SIZE - 1 - j);
			if(tmp == player)
			{
				marksDiag4.player ++;
				marksDiag4.opponent = MINUS_INFINITY;
			}
			else if(tmp == other_player)
			{
				marksDiag4.opponent ++;
				marksDiag4.player = MINUS_INFINITY;
			}
		}

		Pair eval = new Pair();
		eval.player = getPoints(marksDiag1.player, searchDepth) + getPoints(marksDiag2.player, searchDepth) + getPoints(marksDiag3.player, searchDepth) + getPoints(marksDiag4.player, searchDepth);
		eval.opponent = getPoints(marksDiag1.opponent, searchDepth) + getPoints(marksDiag2.opponent, searchDepth) + getPoints(marksDiag3.opponent, searchDepth) + getPoints(marksDiag4.opponent, searchDepth);

    	return eval;
    }

    private int getGameState(GameState gameState, int a, int b, int constantDim, int layer)
    {
    	switch(constantDim)
    	{
    		case 0:
    		return gameState.at(layer, a, b);
    		case 1:
    		return gameState.at(a, layer, b);
    		case 2:
    		return gameState.at(a, b, layer);
    		default:
    		System.err.println("getGameState error");
    	}

    	return 0;
    }

    private int getPoints(int nbMarks, int searchDepth)
    {
    	int nbPoints;

    	if(nbMarks == 4)
    	{
    		nbPoints = 1000000;
            if(searchDepth == 1)
                nbPoints *= 100;
    	}
    	else if(nbMarks > 0)
    		nbPoints = (int)Math.pow(nbMarks, 5);
    	else
    		nbPoints = 0;

    	return nbPoints;
    }
}