package ui.fx;

public final class LineupRow {

    private final String player;
    private final String position;
    private final String role;
    private final String minutes;

    public LineupRow(String player, String position, String role, String minutes) {
        this.player = player;
        this.position = position;
        this.role = role;
        this.minutes = minutes;
    }

    public String getPlayer() { return player; }

    public String getPosition() { return position; }

    public String getRole() { return role; }

    public String getMinutes() { return minutes; }
}
