package model.simulation;

import model.match.FinalMatch;
import model.team.Team;

import java.util.Objects;

public final class FinalMatchReport {

    private final FinalMatch finalMatch;
    private final Team teamA;
    private final Team teamB;
    private final Team champion;
    private final String decidingCriteria;

    public FinalMatchReport(FinalMatch finalMatch, Team teamA, Team teamB, Team champion, String decidingCriteria) {
        this.finalMatch = Objects.requireNonNull(finalMatch, "The final match is required");
        this.teamA = Objects.requireNonNull(teamA, "Team A is required");
        this.teamB = Objects.requireNonNull(teamB, "Team B is required");
        this.champion = Objects.requireNonNull(champion, "The champion is required");
        this.decidingCriteria = Objects.requireNonNull(decidingCriteria, "The deciding criteria is required");
    }

    public FinalMatch getFinalMatch() {
        return finalMatch;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public Team getChampion() {
        return champion;
    }

    public String getDecidingCriteria() {
        return decidingCriteria;
    }
}
