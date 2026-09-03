package ui.fx;

/**
 * Un partido del fixture, listo para mostrar. El resultado llega ya armado
 * ("2 - 1" o un guion si todavia no se jugo).
 */
public final class FixtureRow {

    private final String date;
    private final String homeTeam;
    private final String score;
    private final String awayTeam;
    private final String status;
    private final String referee;

    public FixtureRow(
            String date,
            String homeTeam,
            String score,
            String awayTeam,
            String status,
            String referee) {
        this.date = date;
        this.homeTeam = homeTeam;
        this.score = score;
        this.awayTeam = awayTeam;
        this.status = status;
        this.referee = referee;
    }

    public String getDate() { return date; }

    public String getHomeTeam() { return homeTeam; }

    public String getScore() { return score; }

    public String getAwayTeam() { return awayTeam; }

    public String getStatus() { return status; }

    /** Arbitro designado, o un guion si el partido todavia no se jugo. */
    public String getReferee() { return referee; }
}
