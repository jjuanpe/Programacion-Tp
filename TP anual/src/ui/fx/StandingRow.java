package ui.fx;

/**
 * Una fila de la tabla de posiciones, lista para mostrar.
 */
public final class StandingRow {

    private final int position;
    private final String team;
    private final int played;
    private final int won;
    private final int drawn;
    private final int lost;
    private final int goalsFor;
    private final int goalsAgainst;
    private final int goalDifference;
    private final int points;
    private final boolean qualified;

    public StandingRow(
            int position,
            String team,
            int played,
            int won,
            int drawn,
            int lost,
            int goalsFor,
            int goalsAgainst,
            int goalDifference,
            int points,
            boolean qualified) {
        this.position = position;
        this.team = team;
        this.played = played;
        this.won = won;
        this.drawn = drawn;
        this.lost = lost;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalDifference = goalDifference;
        this.points = points;
        this.qualified = qualified;
    }

    public int getPosition() { return position; }

    public String getTeam() { return team; }

    public int getPlayed() { return played; }

    public int getWon() { return won; }

    public int getDrawn() { return drawn; }

    public int getLost() { return lost; }

    public int getGoalsFor() { return goalsFor; }

    public int getGoalsAgainst() { return goalsAgainst; }

    public int getGoalDifference() { return goalDifference; }

    public int getPoints() { return points; }

    /** Si el equipo entra en los puestos que clasifican a cuartos. */
    public boolean isQualified() { return qualified; }
}
