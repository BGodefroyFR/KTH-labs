public class Guess
{
	public double dist;
	public boolean isSuccess;
	public int guess;
	public int guessIdx;

	public static int pointer = 0;
	public static int currentGuessIdx = 0;

	public Guess(double dist, int guess)
	{
		this.dist = dist;
		this.isSuccess = isSuccess;
		this.guess = guess;
		//this.guessIdx = Player.guessStep;
	}

	public void printCSV()
	{
		System.err.println(dist + "," + (isSuccess ? 1 : 0));
	}
}