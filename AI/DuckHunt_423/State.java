public class State
{
	public int index;
	public double prob;

	public State(int index, double prob)
	{
		this.index = index;
		this.prob = prob;
	}

	public void show()
	{
		System.err.println("State: " + index + " - " + prob);
	}
}