package model.simulation;

import model.match.FirstLegMatch;
import model.match.PhaseType;
import model.match.SecondLegMatch;
import model.team.Team;

import java.util.Objects;

public final class KnockoutTieReport {

    private final PhaseType phase;
    private final FirstLegMatch firstLeg;
    private final SecondLegMatch secondLeg;
    private final Team winner;
    private final String decidingCriteria;
    private final String workerThreadName;

    public KnockoutTieReport(
            PhaseType phase,
            FirstLegMatch firstLeg,
            SecondLegMatch secondLeg,
            Team winner,
            String decidingCriteria,
            String workerThreadName) {
        this.phase = Objects.requireNonNull(phase, "The phase is required");
        this.firstLeg = Objects.requireNonNull(firstLeg, "The first leg is required");
        this.secondLeg = Objects.requireNonNull(secondLeg, "The second leg is required");
        this.winner = Objects.requireNonNull(winner, "The tie winner is required");
        this.decidingCriteria = Objects.requireNonNull(decidingCriteria, "The deciding criteria is required");
        this.workerThreadName = Objects.requireNonNull(workerThreadName, "The worker thread name is required");
    }

    public PhaseType getPhase() {
        return phase;
    }

    public FirstLegMatch getFirstLeg() {
        return firstLeg;
    }

    public SecondLegMatch getSecondLeg() {
        return secondLeg;
    }

    public Team getWinner() {
        return winner;
    }

    public String getDecidingCriteria() {
        return decidingCriteria;
    }

    public String getWorkerThreadName() {
        return workerThreadName;
    }
}
