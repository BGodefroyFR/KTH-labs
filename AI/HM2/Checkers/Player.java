import java.util.*;

public class Player {

    private static final int SEARCH_DEPTH = 3;

    private static final int INFINITY = (int)2e10;
    private static final int MINUS_INFINITY = (int)(-2e10);

    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        int me = (pState.getNextPlayer() == Constants.CELL_RED ? Constants.CELL_WHITE : Constants.CELL_RED);

        int searchDepth = SEARCH_DEPTH;

        GameStateEvaluated bestNextState = minimaxPruning(pState, me, me, searchDepth, searchDepth, (double)MINUS_INFINITY, (double)INFINITY);

        System.err.println("------ " + bestNextState.eval);

        return bestNextState.gameState;

        //Random random = new Random();
        //return lNextStates.elementAt(random.nextInt(lNextStates.size()));
    }

    private GameStateEvaluated minimaxPruning(GameState gameState, int me, int player, int currentSearchDepth, int maxSearchDepth, double alpha, double beta)
    {
        Vector<GameState> nextGameStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextGameStates);

        int other_player = (player == Constants.CELL_RED ? Constants.CELL_WHITE : Constants.CELL_RED);
        int not_me = (me == Constants.CELL_RED ? Constants.CELL_WHITE : Constants.CELL_RED);

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

                    if(beta <= alpha) // beta prune
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

                    if(beta <= alpha) // alpha prune
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
        double gamma = 1.0;
        return gamma;
    }
}
