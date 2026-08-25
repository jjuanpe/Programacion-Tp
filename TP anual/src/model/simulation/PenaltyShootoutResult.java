package model.simulation;

import model.event.PenaltyExecuted;
import model.team.Team;

import java.util.List;
import java.util.Objects;

public final class PenaltyShootoutResult {

    private final Team winner;
    private final List<PenaltyExecuted> kicks;

    public PenaltyShootoutResult(Team winner, List<PenaltyExecuted> kicks) {
        this.winner = Objects.requireNonNull(winner, "The shoot-out winner is required");
        this.kicks = List.copyOf(kicks);
    }

    public Team getWinner() {
        return winner;
    }

    public List<PenaltyExecuted> getKicks() {
        return kicks;
    }
}
