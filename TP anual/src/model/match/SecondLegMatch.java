package model.match;

import model.venue.Stadium;
import model.people.Referee;
import model.team.Team;

import java.time.LocalDate;

public class SecondLegMatch extends Match {

    private PhaseType phase;
    private FirstLegMatch firstLeg;

    public SecondLegMatch(LocalDate date, Team homeTeam, Team awayTeam, Referee referee,
                          Stadium stadium, PhaseType phase, FirstLegMatch firstLeg) {
        super(date, homeTeam, awayTeam, referee, stadium);
        this.phase = phase;
        this.firstLeg = firstLeg;
    }

    public PhaseType getPhase() {
        return phase;
    }

    public FirstLegMatch getFirstLeg() {
        return firstLeg;
    }

    /**
     * Plain aggregate goals (no away-goals weighting) for the team playing
     * as home in this second leg (away in the first leg).
     */
    public int getPlainAggregateForSecondLegHomeTeam() {
        return firstLeg.getAwayGoals() + getHomeGoals();
    }

    /**
     * Plain aggregate goals (no away-goals weighting) for the team playing
     * as away in this second leg (home in the first leg).
     */
    public int getPlainAggregateForSecondLegAwayTeam() {
        return firstLeg.getHomeGoals() + getAwayGoals();
    }

    /**
     * Tie-break aggregate applying the rule that away goals count double,
     * for the team playing as home in this second leg.
     */
    public int getAwayGoalsWeightedAggregateForSecondLegHomeTeam() {
        return (firstLeg.getAwayGoals() * 2) + getHomeGoals();
    }

    /**
     * Tie-break aggregate applying the rule that away goals count double,
     * for the team playing as away in this second leg.
     */
    public int getAwayGoalsWeightedAggregateForSecondLegAwayTeam() {
        return firstLeg.getHomeGoals() + (getAwayGoals() * 2);
    }

    /**
     * True when the plain aggregate score alone already decides the tie
     * (no need to apply the away-goals tie-break rule).
     */
    public boolean isDecidedByPlainAggregate() {
        return getPlainAggregateForSecondLegHomeTeam() != getPlainAggregateForSecondLegAwayTeam();
    }

    /**
     * Resolves the winner of the two-legged tie following the assignment's
     * rule cascade: (1) plain aggregate score, (2) away-goals-weighted
     * aggregate, (3) penalties (returns null, meaning the tie must go to a
     * shoot-out). Written without any loop, so no break/continue is needed.
     */
    public model.team.Team resolveTieWinner() {
        model.team.Team winner;
        if (isDecidedByPlainAggregate()) {
            winner = getPlainAggregateForSecondLegHomeTeam() > getPlainAggregateForSecondLegAwayTeam()
                    ? getHomeTeam()
                    : getAwayTeam();
        } else {
            int weightedHome = getAwayGoalsWeightedAggregateForSecondLegHomeTeam();
            int weightedAway = getAwayGoalsWeightedAggregateForSecondLegAwayTeam();
            if (weightedHome != weightedAway) {
                winner = weightedHome > weightedAway ? getHomeTeam() : getAwayTeam();
            } else {
                winner = null;
            }
        }
        return winner;
    }
}