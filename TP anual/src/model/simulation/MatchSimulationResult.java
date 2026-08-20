package model.simulation;

import model.event.Incidence;
import model.match.Formation;
import model.match.PlayerParticipation;

import java.util.List;
import java.util.Objects;

public final class MatchSimulationResult {
    private final int homeGoals;
    private final int awayGoals;
    private final Formation homeFormation;
    private final Formation awayFormation;
    private final List<PlayerParticipation> participations;
    private final List<Incidence> incidences;

    public MatchSimulationResult(
            int homeGoals,
            int awayGoals,
            Formation homeFormation,
            Formation awayFormation,
            List<PlayerParticipation> participations,
            List<Incidence> incidences) {
        if (homeGoals < 0 || awayGoals < 0) {
            throw new IllegalArgumentException("Goals cannot be negative");
        }
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.homeFormation = Objects.requireNonNull(homeFormation, "The home formation is required");
        this.awayFormation = Objects.requireNonNull(awayFormation, "The away formation is required");
        this.participations = List.copyOf(participations);
        this.incidences = List.copyOf(incidences);
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public Formation getHomeFormation() {
        return homeFormation;
    }

    public Formation getAwayFormation() {
        return awayFormation;
    }

    public List<PlayerParticipation> getParticipations() {
        return participations;
    }

    public List<Incidence> getIncidences() {
        return incidences;
    }
}
