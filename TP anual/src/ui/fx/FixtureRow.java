package ui.fx;

public final class FixtureRow {

    private final String date;
    private final String homeTeam;
    private final String score;
    private final String awayTeam;
    private final String status;
    private final String referee;
    private final String stadium;

    public FixtureRow(
            String date,
            String homeTeam,
            String score,
            String awayTeam,
            String status,
            String referee,
            String stadium) {
        this.date = date;
        this.homeTeam = homeTeam;
        this.score = score;
        this.awayTeam = awayTeam;
        this.status = status;
        this.referee = referee;
        this.stadium = stadium;
    }

    public String getDate() { return date; }

    public String getHomeTeam() { return homeTeam; }

    public String getScore() { return score; }

    public String getAwayTeam() { return awayTeam; }

    public String getStatus() { return status; }

    public String getReferee() { return referee; }

    public String getStadium() { return stadium; }
}
