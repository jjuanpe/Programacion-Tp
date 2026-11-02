package model.event;

import model.match.Match;
import model.people.Player;

import java.util.Objects;

public class PenaltyExecuted extends Incidence {
    private static final long serialVersionUID = 1L;
    private final boolean saved;
    private final Player kicker;
    private final boolean scored;

    public PenaltyExecuted(int minute, Match match, Player kicker, boolean scored) {
        this(minute, match, kicker, scored, false);
    }

    public PenaltyExecuted(int minute, Match match, Player kicker, boolean scored, boolean saved) {
        super(minute, match);
        if (scored && saved) {
            throw new IllegalArgumentException("A scored penalty cannot be saved");
        }
        this.saved = saved;
        this.kicker = Objects.requireNonNull(kicker, "The penalty kicker is required");
        this.scored = scored;
    }

    public Player getKicker() {
        return kicker;
    }

    public boolean isScored() {
        return scored;
    }

    public String getOutcomeDescription() {
        String description;
        if (scored) {
            description = "scored";
        } else if (saved) {
            description = "saved";
        } else {
            description = "off target";
        }
        return description;
    }

    @Override
    public String getDescription() {
        return getMinute() + "' Penalty: " + kicker.getName()
                + (" " + getOutcomeDescription());
    }
}
