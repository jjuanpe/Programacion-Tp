package model.match;

import model.people.Player;

public class PlayerParticipation {
    private final Player player;
    private final boolean starter;
    private int minutesPlayed;

    public PlayerParticipation(Player player, boolean starter) {
        this.player = player;
        this.starter = starter;
        this.minutesPlayed = 0;
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
