package ui.fx;

import java.util.List;

/**
 * Una zona del campeonato, lista para mostrar: su tabla de posiciones, su
 * fixture y quienes clasifican.
 */
public final class GroupZone {

    private final String name;
    private final List<StandingRow> standings;
    private final List<FixtureRow> fixture;
    private final String qualifiedTeams;

    public GroupZone(
            String name,
            List<StandingRow> standings,
            List<FixtureRow> fixture,
            String qualifiedTeams) {
        this.name = name;
        this.standings = List.copyOf(standings);
        this.fixture = List.copyOf(fixture);
        this.qualifiedTeams = qualifiedTeams;
    }

    public String getName() { return name; }

    public List<StandingRow> getStandings() { return standings; }

    public List<FixtureRow> getFixture() { return fixture; }

    /** Los clasificados a cuartos, ya armados como texto. */
    public String getQualifiedTeams() { return qualifiedTeams; }
}
