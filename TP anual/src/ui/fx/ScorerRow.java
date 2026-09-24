package ui.fx;

public final class ScorerRow {

    private final int position;
    private final String player;
    private final String team;
    private final int goals;
    private final int penaltyGoals;

    public ScorerRow(int position, String player, String team, int goals, int penaltyGoals) {
        this.position = position;
        this.player = player;
        this.team = team;
        this.goals = goals;
        this.penaltyGoals = penaltyGoals;
    }

    public int getPosition() { return position; }

    public String getPlayer() { return player; }

    public String getTeam() { return team; }

    public int getGoals() { return goals; }

    public int getPenaltyGoals() { return penaltyGoals; }
}
