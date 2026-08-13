package model;

public class Change extends Incidence{
    private Player playerOut;
    private Player playerIn;

    public Change(int minute, Match match, Player playerOut, Player playerIn) {
        super(minute, match);
        this.playerOut = playerOut;
        this.playerIn = playerIn;
    }

    public Player getPlayerOut() {
        return playerOut;
    }

    public void setPlayerOut(Player playerOut) {
        this.playerOut = playerOut;
    }

    public Player getPlayerIn() {
        return playerIn;
    }

    public void setPlayerIn(Player playerIn) {
        this.playerIn = playerIn;
    }
}
