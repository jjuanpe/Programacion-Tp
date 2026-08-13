package model.event;

import model.match.Match;
import model.people.Player;

public class PenaltyExecuted extends Incidence {
    private Player kicker;
    private boolean scored;

    public PenaltyExecuted(int minute, Match match,boolean scored) {
        super(minute, match);
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
