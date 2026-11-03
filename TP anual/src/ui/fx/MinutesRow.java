package ui.fx;

public final class MinutesRow {

    private final int position;
    private final String player;
    private final String team;
    private final String playerPosition;
    private final int matchesPlayed;
    private final int minutesPlayed;

    public MinutesRow(int position, String player, String team, String playerPosition,
                      int matchesPlayed, int minutesPlayed) {
        this.position = position;
        this.player = player;
        this.team = team;
        this.playerPosition = playerPosition;
        this.matchesPlayed = matchesPlayed;
        this.minutesPlayed = minutesPlayed;
    }

    public int getPosition() { return position; }

    public String getPlayer() { return player; }

    public String getTeam() { return team; }

    public String getPlayerPosition() { return playerPosition; }

    public int getMatchesPlayed() { return matchesPlayed; }

    public int getMinutesPlayed() { return minutesPlayed; }
}