package model.event;

import model.match.Match;
import model.people.Player;

import java.util.Objects;

public class YellowCard extends Incidence {
    private final Player player;

    public YellowCard(int minute, Match match, Player player) {
        super(minute, match);
        this.player = Objects.requireNonNull(player, "The cautioned player is required");
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getDescription() {
        return getMinute() + "' Yellow card: " + player.getName();
    }
}
