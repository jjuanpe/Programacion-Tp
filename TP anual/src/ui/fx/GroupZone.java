package ui.fx;

import java.util.List;

public final class GroupZone {

    private final String name;
    private final String teams;
    private final List<StandingRow> standings;
    private final List<FixtureRow> fixture;
    private final String qualifiedTeams;

    public GroupZone(
            String name,
            String teams,
            List<StandingRow> standings,
            List<FixtureRow> fixture,
            String qualifiedTeams) {
        this.name = name;
        this.teams = teams;
        this.standings = List.copyOf(standings);
        this.fixture = List.copyOf(fixture);
        this.qualifiedTeams = qualifiedTeams;
    }

    public String getName() { return name; }

    public String getTeams() { return teams; }

    public List<StandingRow> getStandings() { return standings; }

    public List<FixtureRow> getFixture() { return fixture; }

    public String getQualifiedTeams() { return qualifiedTeams; }
}
