package model.event;

import model.match.Match;
import model.people.Player;
import java.util.Objects;

/** A non-scoring action: foul, woodwork or unsuccessful attempt. */
public final class PlayEvent extends Incidence {
    private static final long serialVersionUID = 1L;
    public static final String FOUL = "Foul";
    public static final String WOODWORK = "Shot hit the woodwork";
    public static final String SHOT_SAVED = "Shot saved";
    public static final String SHOT_OFF_TARGET = "Shot off target";
    private final Player player;
    private final String type;

    public PlayEvent(int minute, Match match, Player player, String type) {
        super(minute, match);
        this.player = Objects.requireNonNull(player, "The player is required");
        if (!FOUL.equals(type) && !WOODWORK.equals(type)
                && !SHOT_SAVED.equals(type) && !SHOT_OFF_TARGET.equals(type)) {
            throw new IllegalArgumentException("Unknown play event type");
        }
        this.type = type;
    }

    public Player getPlayer() { return player; }
    public String getType() { return type; }

    @Override
    public String getTypeLabel() {
        return type;
    }

    @Override
    public String getDetail() {
        return describeWithTeam(player);
    }

    @Override
    public String getDescription() {
        return getMinute() + "' " + type + ": " + player.getName();
    }
}
