package model.event;

import model.match.Match;
import model.people.Player;

import java.util.Objects;

public class Change extends Incidence {
    private final Player playerOut;
    private final Player playerIn;

    public Change(int minute, Match match, Player playerOut, Player playerIn) {
        super(minute, match);
        this.playerOut = Objects.requireNonNull(playerOut, "The outgoing player is required");
        this.playerIn = Objects.requireNonNull(playerIn, "The incoming player is required");
    }

    public Player getPlayerOut() {
        return playerOut;
    }

    public Player getPlayerIn() {
        return playerIn;
    }

    @Override
    public String getDescription() {
        return getMinute() + "' Change: " + playerOut.getName() + " -> " + playerIn.getName();
    }
}
