public class Pair {
 
    public int player;
    public int opponent;

    public Pair()
    {
        this.player = 0;
        this.opponent = 0;
    }

    public Pair(int player, int opponent)
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