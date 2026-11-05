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

    public int getPointsForSecondLegHomeTeam() {
        return pointsOf(firstLeg.getAwayGoals(), firstLeg.getHomeGoals())
                + pointsOf(getHomeGoals(), getAwayGoals());
    }

    public int getPointsForSecondLegAwayTeam() {
        return pointsOf(firstLeg.getHomeGoals(), firstLeg.getAwayGoals())
                + pointsOf(getAwayGoals(), getHomeGoals());
    }

    private static int pointsOf(int goalsScored, int goalsConceded) {
        int points = 0;
        if (goalsScored > goalsConceded) {
            points = 3;
        } else if (goalsScored == goalsConceded) {
            points = 1;
        }
        return points;
    }

    public TieCriterion getDecidingCriterion() {
        TieCriterion criterion = TieCriterion.PENALTY_SHOOTOUT;
        if (getPointsForSecondLegHomeTeam() != getPointsForSecondLegAwayTeam()) {
            criterion = TieCriterion.POINTS;
        } else if (getAwayGoalsWeightedAggregateForSecondLegHomeTeam()
                != getAwayGoalsWeightedAggregateForSecondLegAwayTeam()) {
            criterion = TieCriterion.GOAL_DIFFERENCE;
        }
        return criterion;
    }

    public Team resolveTieWinner() {
        Team winner = null;
        TieCriterion criterion = getDecidingCriterion();
        if (criterion == TieCriterion.POINTS) {
            winner = getPointsForSecondLegHomeTeam() > getPointsForSecondLegAwayTeam()
                    ? getHomeTeam() : getAwayTeam();
        } else if (criterion == TieCriterion.GOAL_DIFFERENCE) {
            winner = getAwayGoalsWeightedAggregateForSecondLegHomeTeam()
                    > getAwayGoalsWeightedAggregateForSecondLegAwayTeam()
                    ? getHomeTeam() : getAwayTeam();
        }
        return winner;
    }

    @Override
    public String describeAggregate() {
        String aggregate = "";
        if (isPlayed() && firstLeg.isPlayed()) {
            aggregate = new StringBuilder("Aggregate: ")
                    .append(getPlainAggregateForSecondLegHomeTeam()).append(" - ")
                    .append(getPlainAggregateForSecondLegAwayTeam()).toString();
        }
        return aggregate;
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