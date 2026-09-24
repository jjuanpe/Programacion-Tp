package model.competition;

import model.team.Country;
import model.team.Team;

import java.util.Objects;

public class TeamReportEntry {

    private static final int POINTS_PER_WIN = 3;
    private static final int POINTS_PER_DRAW = 1;

    private final Team team;
    private final double averagePlayerAge;
    private final int coachAge;
    private final Country coachCountry;

    private int played;
    private int points;
    private int goalsFor;
    private int goalsAgainst;

    TeamReportEntry(Team team, double averagePlayerAge, int coachAge, Country coachCountry) {
        this.team = Objects.requireNonNull(team, "The team is required");
        this.averagePlayerAge = averagePlayerAge;
        this.coachAge = coachAge;
        this.coachCountry = coachCountry;
    }

    void registerMatch(int scored, int conceded) {
        played++;
        goalsFor += scored;
        goalsAgainst += conceded;
        if (scored > conceded) {
            points += POINTS_PER_WIN;
        } else if (scored == conceded) {
            points += POINTS_PER_DRAW;
        }
    }

    public Team getTeam() { return team; }

    public double getAveragePlayerAge() { return averagePlayerAge; }

    public int getCoachAge() { return coachAge; }

    public Country getCoachCountry() { return coachCountry; }

    public int getPlayed() { return played; }

    public int getPoints() { return points; }

    public int getPossiblePoints() { return played * POINTS_PER_WIN; }

    public int getGoalsFor() { return goalsFor; }

    public int getGoalsAgainst() { return goalsAgainst; }

    public double getEffectiveness() {
        double effectiveness = 0;
        if (played > 0) {
            effectiveness = points * 100.0 / getPossiblePoints();
        }
        return effectiveness;
    }
}
