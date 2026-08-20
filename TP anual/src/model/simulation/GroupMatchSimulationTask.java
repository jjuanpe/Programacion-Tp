package model.simulation;

import model.match.GroupMatch;
import model.people.Player;

import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GroupMatchSimulationTask implements Runnable {
    private final GroupMatch match;
    private final Set<Player> suspendedPlayers;
    private final MatchSimulationStrategy<GroupMatch> simulationStrategy;
    private final long seed;
    private MatchSimulationResult result;
    private String workerThreadName;

    public GroupMatchSimulationTask(
            GroupMatch match,
            Set<Player> suspendedPlayers,
            MatchSimulationStrategy<GroupMatch> simulationStrategy,
            long seed) {
        this.match = Objects.requireNonNull(match, "The match is required");
        this.suspendedPlayers = suspendedPlayers == null ? Set.of() : Set.copyOf(suspendedPlayers);
        this.simulationStrategy = Objects.requireNonNull(simulationStrategy, "The simulation strategy is required");
        this.seed = seed;
    }

    @Override
    public void run() {
        workerThreadName = Thread.currentThread().getName();
        result = simulationStrategy.simulate(match, suspendedPlayers, new Random(seed));
    }

    public MatchSimulationResult getResult() {
        if (result == null) {
            throw new IllegalStateException("The simulation task has not finished yet");
        }
        return result;
    }

    public String getWorkerThreadName() {
        if (workerThreadName == null) {
            throw new IllegalStateException("The simulation task has not started yet");
        }
        return workerThreadName;
    }
}
