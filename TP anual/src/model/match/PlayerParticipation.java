package model.match;

import model.people.Player;

public class PlayerParticipation {
    private final Player player;
    private final boolean starter;
    private final int minutesPlayed;

    public PlayerParticipation(Player player, boolean starter) {
        this(player, starter, 0);
    }

    public PlayerParticipation(Player player, boolean starter, int minutesPlayed) {
        if (player == null) {
            throw new IllegalArgumentException("The player is required");
        }
        if (minutesPlayed < 0 || minutesPlayed > 90) {
            throw new IllegalArgumentException("Minutes played must be between 0 and 90");
        }
        this.player = player;
        this.starter = starter;
        this.minutesPlayed = minutesPlayed;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isStarter() {
        return starter;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }

}
