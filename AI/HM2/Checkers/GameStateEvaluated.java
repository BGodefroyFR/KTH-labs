public class GameStateEvaluated {
 
    public GameState gameState;
    public double eval;

    public GameStateEvaluated(GameState gameState, double eval)
    {
    	this.gameState = gameState;
    	this.eval = eval;
    }

    public GameStateEvaluated(GameState gameState)
    {
    	this.gameState = gameState;
    }

    public GameStateEvaluated(double eval)
    {
    	this.eval = eval;
    }
}