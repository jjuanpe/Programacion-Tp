package model.event;

import model.people.Player;

import java.util.Objects;

public class YellowCard extends Incidence {
    private static final long serialVersionUID = 1L;
    private final Player player;

    public YellowCard(int minute, IncidenceContext match, Player player) {
        super(minute, match);
        this.player = Objects.requireNonNull(player, "The cautioned player is required");
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getTypeLabel() {
        return "Yellow card";
    }

    @Override
    public String getDetail() {
        return describeWithTeam(player);
    }

    @Override
    public String getDescription() {
        return new StringBuilder().append(getMinute()).append("' Yellow card: ")
                .append(player.getName()).toString();
    }
}
