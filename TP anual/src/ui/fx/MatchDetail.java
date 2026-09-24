package ui.fx;

import java.util.List;

public final class MatchDetail {

    private final String phase;
    private final String phaseCategory;
    private final String date;
    private final String homeTeam;
    private final String awayTeam;
    private final String score;
    private final String status;
    private final boolean played;
    private final String referee;
    private final String stadium;
    private final String aggregate;
    private final List<LineupRow> homeLineup;
    private final List<LineupRow> awayLineup;
    private final List<IncidenceRow> incidences;

    public MatchDetail(
            String phase,
            String phaseCategory,
            String date,
            String homeTeam,
            String awayTeam,
            String score,
            String status,
            boolean played,
            String referee,
            String stadium,
            String aggregate,
            List<LineupRow> homeLineup,
            List<LineupRow> awayLineup,
            List<IncidenceRow> incidences) {
        this.phase = phase;
        this.phaseCategory = phaseCategory;
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = score;
        this.status = status;
        this.played = played;
        this.referee = referee;
        this.stadium = stadium;
        this.aggregate = aggregate;
        this.homeLineup = List.copyOf(homeLineup);
        this.awayLineup = List.copyOf(awayLineup);
        this.incidences = List.copyOf(incidences);
    }

    public String getPhase() { return phase; }

    public String getPhaseCategory() { return phaseCategory; }

    public String getDate() { return date; }

    public String getHomeTeam() { return homeTeam; }

    public String getAwayTeam() { return awayTeam; }

    public String getScore() { return score; }

    public String getStatus() { return status; }

    public boolean isPlayed() { return played; }

    public String getReferee() { return referee; }

    public String getStadium() { return stadium; }

    public String getAggregate() { return aggregate; }

    public List<LineupRow> getHomeLineup() { return homeLineup; }

    public List<LineupRow> getAwayLineup() { return awayLineup; }

    public List<IncidenceRow> getIncidences() { return incidences; }

    public String getTitle() {
        return homeTeam + "  " + score + "  " + awayTeam;
    }
}
