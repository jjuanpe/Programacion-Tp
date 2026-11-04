package model.event;

import model.match.Match;
import model.people.Player;

import java.util.Objects;

public class Expulsion extends Incidence {
    private static final long serialVersionUID = 1L;
    private final Player player;
    private final ExpulsionReason reason;

    public Expulsion(int minute, Match match, Player player) {
        this(minute, match, player, ExpulsionReason.DIRECT_RED_CARD);
    }

    public Expulsion(int minute, Match match, Player player, ExpulsionReason reason) {
        super(minute, match);
        this.player = Objects.requireNonNull(player, "The expelled player is required");
        this.reason = Objects.requireNonNull(reason, "The expulsion reason is required");
    }

    public Player getPlayer() {
        return player;
    }

    public ExpulsionReason getReason() {
        return reason;
    }

    @Override
    public String getTypeLabel() {
        return "Expulsion";
    }

    @Override
    public String getDetail() {
        StringBuilder detail = new StringBuilder(describeWithTeam(player));
        detail.append(" (").append(reason.getDescription()).append(')');
        if (getMatch().findFifthExpulsion(getMatch().getIncidences()) == this) {
            detail.append(" - Fifth red card: match abandoned, team loses 0-3");
        }
        return detail.toString();
    }

    @Override
    public String getDescription() {
        return getMinute() + "' Expulsion (" + reason.getDescription() + "): " + player.getName();
    }
}
