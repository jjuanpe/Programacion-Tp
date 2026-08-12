package model;

public class PenaltyExecuted extends Incidence{
    private Player kicker;
    private boolean scored;

    public Penalty(int minute, Match match, Player kicker, boolean scored) {
        super(minute, match);
        this.kicker = kicker;
        this.scored = scored;
    }

    public Player getKicker() {
        return kicker;
    }

    public void setKicker(Player kicker) {
        this.kicker = kicker;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }
}
