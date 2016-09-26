public class GameStateEvaluated {
 
    public GameState gameState;
    public int eval;

    public GameStateEvaluated(GameState gameState, int eval)
    {
    	this.gameState = gameState;
    	this.eval = eval;
    }

    public GameStateEvaluated(GameState gameState)
    {
    	this.gameState = gameState;
    }

    public GameStateEvaluated(int eval)
    {
    	this.eval = eval;
    }
}