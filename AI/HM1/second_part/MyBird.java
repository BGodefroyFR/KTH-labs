public class MyBird
{
	public Model model;
	public int[] obs;
	public State nextPosition;

	public MyBird(int nbHiddenState, int nbObs)
	{
		model = new Model(nbHiddenState, nbObs);
		obs = new int[99];
	}

	public void estimateModel(int step)
	{
		model.randomize();
		model = HMM.estimateModel(model, obs, step);
	}

	public void computeNextPosition(int step, boolean show)
	{
		nextPosition = HMM.getMostLikelyLastHiddenState(model, Util.createMatrixFromArray1D(obs, step));
		/*if(show) {
			System.err.println(nextPosition.index + " - " + nextPosition.prob);
			model.show();
			for(int i = 0; i < obs.length && i<= step; i++)
				System.err.println(obs[i]);
		}*/
	}
}