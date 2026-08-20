package model.event;

import model.match.Match;
import model.people.Player;

import java.util.Objects;

public class PenaltyExecuted extends Incidence {
    private final Player kicker;
    private final boolean scored;

    public PenaltyExecuted(int minute, Match match, Player kicker, boolean scored) {
        super(minute, match);
        this.kicker = Objects.requireNonNull(kicker, "The penalty kicker is required");
        this.scored = scored;
    }

    public Player getKicker() {
        return kicker;
    }

    public boolean isScored() {
        return scored;
    }

    @Override
    public String getDescription() {
        return getMinute() + "' Penalty: " + kicker.getName()
                + (scored ? " scored" : " missed");
    }
}
