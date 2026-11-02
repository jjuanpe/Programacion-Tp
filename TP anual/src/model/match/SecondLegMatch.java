package model.match;

import model.venue.Stadium;
import model.people.Referee;
import model.team.Team;

import java.time.LocalDate;

public class SecondLegMatch extends Match {
    private static final long serialVersionUID = 1L;
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

    public int getPlainAggregateForSecondLegHomeTeam() {
        return firstLeg.getAwayGoals() + getHomeGoals();
    }

    public int getPlainAggregateForSecondLegAwayTeam() {
        return firstLeg.getHomeGoals() + getAwayGoals();
    }

    public int getAwayGoalsWeightedAggregateForSecondLegHomeTeam() {
        return (firstLeg.getAwayGoals() * 2) + getHomeGoals();
    }

    public int getAwayGoalsWeightedAggregateForSecondLegAwayTeam() {
        return firstLeg.getHomeGoals() + (getAwayGoals() * 2);
    }

    public boolean isDecidedByPlainAggregate() {
        return getPlainAggregateForSecondLegHomeTeam() != getPlainAggregateForSecondLegAwayTeam();
    }

    public Team resolveTieWinner() {
        Team winner;
        if (isDecidedByPlainAggregate()) {
            if (getPlainAggregateForSecondLegHomeTeam() > getPlainAggregateForSecondLegAwayTeam()) {
                winner = getHomeTeam();
            } else {
                winner = getAwayTeam();
            }
        } else {
            winner = null;
        }
        return winner;
    }

    @Override
    public boolean allowsPenaltyShootout() {
        return true;
    }

    @Override
    public String getPhaseLabel() {
        return phase.getLabel() + " (2nd leg)";
    }

    @Override
    public String getPhaseCategory() {
        return phase.getLabel();
    }
}