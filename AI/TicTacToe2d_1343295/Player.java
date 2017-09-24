import java.util.*;

public class Player {

    private static final int SEARCH_DEPTH = 6;
    private static final int INFINITY = (int)2e10;

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

        int me = (gameState.getNextPlayer() == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X);

        GameStateEvaluated bestNextState = minimax(gameState, me, me, SEARCH_DEPTH);

        return bestNextState.gameState;
    }

    //getNextPlayer

    private GameStateEvaluated minimax(GameState gameState, int me, int player, int searchDepth)
    {
    	Vector<GameState> nextGameStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextGameStates);

        if(nextGameStates.size() == 0 || searchDepth == 0)
        {
        	GameStateEvaluated leaf = new GameStateEvaluated(gameState, (int)(Math.pow(evaluate(gameState, player), 1) /*- Math.pow(evaluate(gameState, (player == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X)), 3) * 1.2*/));
        	return leaf;
        }
        else
        {
        	int other_player = (player == Constants.CELL_X ? Constants.CELL_O : Constants.CELL_X);

        	GameStateEvaluated bestPossible = new GameStateEvaluated((me == player ? (-1)*INFINITY : INFINITY));
        	GameStateEvaluated bestNextMove = null;

    		for(GameState child: nextGameStates)
    		{
    			GameStateEvaluated v = minimax(child, me, other_player, searchDepth-1);

    			if((me == player && v.eval > bestPossible.eval) || (me != player && v.eval < bestPossible.eval))
    			{
    				bestPossible = v;

    				if(searchDepth == SEARCH_DEPTH)
    				{
    					bestNextMove = new GameStateEvaluated(child, v.eval);
    				}
    			}
    		}

    		if(searchDepth == SEARCH_DEPTH)
    			return bestNextMove;
    		else
    			return bestPossible;
        }
    }

    private int evaluate(GameState gameState, int player)
    {
        int eval = 0;

        for(int i = 0; i < GameState.BOARD_SIZE; i++)
        {
            int maxMarksRow = 0;
            int currentMarksRow = 0;
            int maxMarksColumn = 0;
            int currentMarksColumn = 0;
            for(int j = 0; j < GameState.BOARD_SIZE; j++)
            {
                // Rows
                if(gameState.at(i, j) == player)
                {
                    currentMarksRow ++;
                    if(currentMarksRow > maxMarksRow)
                        maxMarksRow = currentMarksRow;
                }
                else
                    currentMarksRow = 0;

                // Columns
                if(gameState.at(j, i) == player)
                {
                    currentMarksColumn ++;
                    if(currentMarksColumn > maxMarksColumn)
                        maxMarksColumn = currentMarksColumn;
                }
                else
                    currentMarksColumn = 0;
            }

            eval += maxMarksRow + maxMarksColumn;
        }

        // Diagonals
       for(int i = -GameState.BOARD_SIZE + 1; i < GameState.BOARD_SIZE; i++)
       {
            int maxMarksDiag1 = 0;
            int currentMarksDiag1 = 0;
            int maxMarksDiag2 = 0;
            int currentMarksDiag2 = 0;

            int init = (i >= 0 ? 0 : (-1)*i);

            for(int j = init; j < GameState.BOARD_SIZE && (i + j) < GameState.BOARD_SIZE; j++)
            {
                if(gameState.at(i + j, j) == player)
                {
                    currentMarksDiag1 ++;
                    if(currentMarksDiag1 > maxMarksDiag1)
                        maxMarksDiag1 = currentMarksDiag1;
                }
                else
                    currentMarksDiag1 = 0;

                if(gameState.at(i + j, GameState.BOARD_SIZE - 1 - j) == player)
                {
                    currentMarksDiag2 ++;
                    if(currentMarksDiag2 > maxMarksDiag2)
                        maxMarksDiag2 = currentMarksDiag2;
                }
                else
                    currentMarksDiag2 = 0;
            }
            
            eval += maxMarksDiag1 + maxMarksDiag2;
       }

       return eval;
    }
}