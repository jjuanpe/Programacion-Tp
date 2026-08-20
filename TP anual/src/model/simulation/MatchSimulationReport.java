package model.simulation;

import model.competition.StandingEntry;
import model.match.GroupMatch;

import java.util.List;
import java.util.Objects;

public final class MatchSimulationReport {
    private final String zoneName;
    private final GroupMatch match;
    private final List<StandingEntry> standingsBefore;
    private final List<StandingEntry> standingsAfter;
    private final String workerThreadName;

    public MatchSimulationReport(
            String zoneName,
            GroupMatch match,
            List<StandingEntry> standingsBefore,
            List<StandingEntry> standingsAfter,
            String workerThreadName) {
        this.zoneName = Objects.requireNonNull(zoneName, "The zone name is required");
        this.match = Objects.requireNonNull(match, "The match is required");
        this.standingsBefore = List.copyOf(standingsBefore);
        this.standingsAfter = List.copyOf(standingsAfter);
        this.workerThreadName = Objects.requireNonNull(workerThreadName, "The worker name is required");
    }

    public String getZoneName() {
        return zoneName;
    }

    public GroupMatch getMatch() {
        return match;
    }

    public List<StandingEntry> getStandingsBefore() {
        return standingsBefore;
    }

    public List<StandingEntry> getStandingsAfter() {
        return standingsAfter;
    }

    public String getWorkerThreadName() {
        return workerThreadName;
    }
}
