package model.competition;

import model.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builds the quarter-final bracket from the four zones' standings, following
 * the assignment's fixed pairing rule:
 * I. 1st Zone1 vs 2nd Zone4     III. 1st Zone3 vs 2nd Zone1
 * II. 1st Zone2 vs 2nd Zone3    IV. 1st Zone4 vs 2nd Zone2
 */
public class KnockoutBracketService {

    private static final int EXPECTED_ZONE_COUNT = 4;
    private static final int QUALIFIED_PER_ZONE = 2;

    private final StandingsService standingsService;

    public KnockoutBracketService() {
        this(new StandingsService());
    }

    public KnockoutBracketService(StandingsService standingsService) {
        this.standingsService = Objects.requireNonNull(standingsService, "The standings service is required");
    }

    public List<Team[]> buildQuarterFinalPairings(List<Zone> zones) {
        Objects.requireNonNull(zones, "The zone list is required");
        if (zones.size() != EXPECTED_ZONE_COUNT) {
            throw new IllegalArgumentException("The knockout stage requires exactly four zones");
        }

        List<List<Team>> qualifiedByZone = new ArrayList<>();
        for (Zone zone : zones) {
            qualifiedByZone.add(getQualifiedTeams(zone));
        }

        List<Team[]> pairings = new ArrayList<>();
        pairings.add(new Team[]{qualifiedByZone.get(0).get(0), qualifiedByZone.get(3).get(1)}); // I
        pairings.add(new Team[]{qualifiedByZone.get(1).get(0), qualifiedByZone.get(2).get(1)}); // II
        pairings.add(new Team[]{qualifiedByZone.get(2).get(0), qualifiedByZone.get(0).get(1)}); // III
        pairings.add(new Team[]{qualifiedByZone.get(3).get(0), qualifiedByZone.get(1).get(1)}); // IV
        return pairings;
    }

    private List<Team> getQualifiedTeams(Zone zone) {
        List<StandingEntry> standings = standingsService.computeStandings(zone);
        if (standings.size() < QUALIFIED_PER_ZONE) {
            throw new IllegalStateException(
                    "Zone " + zone.getName() + " does not have enough teams to qualify two of them");
        }
        List<Team> qualified = new ArrayList<>();
        qualified.add(standings.get(0).getTeam());
        qualified.add(standings.get(1).getTeam());
        return qualified;
    }
}
