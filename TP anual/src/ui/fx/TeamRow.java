package ui.fx;

public final class TeamRow {

    private final String team;
    private final String averagePlayerAge;
    private final String coachAge;
    private final String coachNationality;
    private final int goalsFor;
    private final int goalsAgainst;
    private final String effectiveness;

    public TeamRow(
            String team,
            String averagePlayerAge,
            String coachAge,
            String coachNationality,
            int goalsFor,
            int goalsAgainst,
            String effectiveness) {
        this.team = team;
        this.averagePlayerAge = averagePlayerAge;
        this.coachAge = coachAge;
        this.coachNationality = coachNationality;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.effectiveness = effectiveness;
    }

    public String getTeam() { return team; }

    public String getAveragePlayerAge() { return averagePlayerAge; }

    public String getCoachAge() { return coachAge; }

    public String getCoachNationality() { return coachNationality; }

    public int getGoalsFor() { return goalsFor; }

    public int getGoalsAgainst() { return goalsAgainst; }

    public String getEffectiveness() { return effectiveness; }
}
