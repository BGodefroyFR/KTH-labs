public class Pair {
 
    public double player;
    public double opponent;

    public Pair()
    {
        this.player = 0;
        this.opponent = 0;
    }

    public Pair(double player, double opponent)
    {
    	this.player = player;
    	this.opponent = opponent;
    }

    public void add(Pair p)
    {
        this.player += p.player;
        this.opponent += p.opponent;
    }
}