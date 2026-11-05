package model.event;

import model.people.Player;

import java.util.List;
import java.util.Objects;

public class Change extends Incidence {
    private static final long serialVersionUID = 1L;
    private final Player playerOut;
    private final Player playerIn;

    public Change(int minute, IncidenceContext match, Player playerOut, Player playerIn) {
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
    public void updateOnPitch(List<Player> onPitch, List<Player> substitutes) {
        onPitch.remove(playerOut);
        if (substitutes.contains(playerIn)) {
            onPitch.add(playerIn);
        }
    }

    @Override
    public String getTypeLabel() {
        return "Substitution";
    }

    @Override
    public String getDetail() {
        return new StringBuilder(describeWithTeam(playerOut))
                .append(" -> ").append(playerIn.getName()).toString();
    }

    @Override
    public String getDescription() {
        return new StringBuilder().append(getMinute()).append("' Change: ").append(playerOut.getName())
                .append(" -> ").append(playerIn.getName()).toString();
    }
}
