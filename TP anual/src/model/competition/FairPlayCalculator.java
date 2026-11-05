package model.competition;

import model.event.Incidence;
import model.match.Match;
import model.team.Team;

import java.util.List;
import java.util.Objects;

public class FairPlayCalculator {
    public int calculate(Zone zone, Team team) {
        Objects.requireNonNull(zone, "The zone is required");
        Objects.requireNonNull(team, "The team is required");
        if (!zone.getTeams().contains(team)) {
            throw new IllegalArgumentException("The team does not belong to the zone");
        }
        return calculate(team, zone.getGroupMatches());
    }

    public int calculate(Team team, List<? extends Match> matches) {
        Objects.requireNonNull(team, "The team is required");
        Objects.requireNonNull(matches, "The match list is required");

        int fairPlayPoints = 0;
        for (Match match : matches) {
            if (match.isPlayed()) {
                for (Incidence incidence : match.getIncidences()) {
                    fairPlayPoints += incidence.fairPlayPointsFor(team);
                }
            }
        }
        return fairPlayPoints;
    }
}
