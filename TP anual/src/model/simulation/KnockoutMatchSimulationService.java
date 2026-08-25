package model.simulation;

import model.event.Expulsion;
import model.event.Goal;
import model.event.Incidence;
import model.match.Formation;
import model.match.Match;
import model.match.PlayerParticipation;
import model.people.Player;
import model.people.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * Simulates the 90 minutes of a knockout stage match (first leg, second leg
 * or final). Reuses {@link FormationService} and {@link TeamStrengthCalculator}
 * from Sprint 3, applying the same non-deterministic scoring approach used
 * for the group stage, simplified to goals and (at most one) expulsion per
 * team, since knockout matches do not require the fuller substitution/card
 * report that the group stage does.
 */
public class KnockoutMatchSimulationService implements MatchSimulationStrategy<Match> {

    private static final int MATCH_MINUTES = 90;
    private static final double EXPULSION_PROBABILITY = 0.08;
    private static final double PENALTY_GOAL_PROBABILITY = 0.15;
    private static final double OWN_GOAL_PROBABILITY = 0.05;
    private static final double HOME_ADVANTAGE = 0.05;
    private static final double SURPRISE_VARIATION = 0.30;
    private static final double MINIMUM_GOAL_SHARE = 0.20;
    private static final double MAXIMUM_GOAL_SHARE = 0.80;

    private final FormationService formationService;
    private final TeamStrengthCalculator strengthCalculator;

    public KnockoutMatchSimulationService() {
        this(new FormationService(), new TeamStrengthCalculator());
    }

    public KnockoutMatchSimulationService(
            FormationService formationService,
            TeamStrengthCalculator strengthCalculator) {
        this.formationService = Objects.requireNonNull(formationService, "The formation service is required");
        this.strengthCalculator = Objects.requireNonNull(strengthCalculator, "The strength calculator is required");
    }

    @Override
    public MatchSimulationResult simulate(Match match, Set<Player> suspendedPlayers, Random random) {
        Objects.requireNonNull(match, "The match is required");
        Objects.requireNonNull(random, "The random generator is required");
        if (match.isPlayed()) {
            throw new IllegalStateException("A played match cannot be simulated again");
        }
        if (match.getReferee() == null) {
            throw new IllegalStateException("The match must have an assigned referee");
        }

        Set<Player> suspended = suspendedPlayers == null ? Set.of() : Set.copyOf(suspendedPlayers);
        Formation homeFormation = formationService.createFormation(match.getHomeTeam(), suspended);
        Formation awayFormation = formationService.createFormation(match.getAwayTeam(), suspended);

        double homeStrength = strengthCalculator.calculate(match.getHomeTeam(), homeFormation);
        double awayStrength = strengthCalculator.calculate(match.getAwayTeam(), awayFormation);
        int totalGoals = random.nextInt(3) + random.nextInt(3);
        double homeGoalShare = calculateHomeGoalShare(homeStrength, awayStrength, random);

        Map<Player, Integer> homeExpulsionMinutes = new HashMap<>();
        Map<Player, Integer> awayExpulsionMinutes = new HashMap<>();
        List<Incidence> incidences = new ArrayList<>();
        addExpulsionIfAny(match, homeFormation, random, incidences, homeExpulsionMinutes);
        addExpulsionIfAny(match, awayFormation, random, incidences, awayExpulsionMinutes);

        int homeGoals = 0;
        int awayGoals = 0;
        for (int goalNumber = 0; goalNumber < totalGoals; goalNumber++) {
            boolean homeScores = random.nextDouble() < homeGoalShare;
            Formation attackingFormation = homeScores ? homeFormation : awayFormation;
            Formation defendingFormation = homeScores ? awayFormation : homeFormation;
            Map<Player, Integer> attackingExpulsions = homeScores ? homeExpulsionMinutes : awayExpulsionMinutes;
            incidences.add(createGoal(match, attackingFormation, defendingFormation, attackingExpulsions, random));
            homeGoals = homeScores ? homeGoals + 1 : homeGoals;
            awayGoals = homeScores ? awayGoals : awayGoals + 1;
        }

        List<PlayerParticipation> participations = new ArrayList<>();
        participations.addAll(buildParticipations(homeFormation, homeExpulsionMinutes));
        participations.addAll(buildParticipations(awayFormation, awayExpulsionMinutes));

        return new MatchSimulationResult(
                homeGoals, awayGoals, homeFormation, awayFormation, participations, incidences);
    }

    private double calculateHomeGoalShare(double homeStrength, double awayStrength, Random random) {
        double totalStrength = homeStrength + awayStrength;
        double strengthShare = totalStrength == 0 ? 0.50 : homeStrength / totalStrength;
        double surprise = random.nextDouble() * SURPRISE_VARIATION - SURPRISE_VARIATION / 2.0;
        double result = strengthShare + HOME_ADVANTAGE + surprise;
        return Math.max(MINIMUM_GOAL_SHARE, Math.min(MAXIMUM_GOAL_SHARE, result));
    }

    private void addExpulsionIfAny(
            Match match,
            Formation formation,
            Random random,
            List<Incidence> incidences,
            Map<Player, Integer> expulsionMinutes) {
        if (random.nextDouble() < EXPULSION_PROBABILITY) {
            List<Player> outfieldPlayers = getOutfieldPlayers(formation.getStarters());
            Player expelledPlayer = outfieldPlayers.get(random.nextInt(outfieldPlayers.size()));
            int minute = 20 + random.nextInt(65);
            incidences.add(new Expulsion(minute, match, expelledPlayer));
            expulsionMinutes.put(expelledPlayer, minute);
        }
    }

    private Goal createGoal(
            Match match,
            Formation attackingFormation,
            Formation defendingFormation,
            Map<Player, Integer> attackingExpulsionMinutes,
            Random random) {
        int minute = 1 + random.nextInt(MATCH_MINUTES);
        boolean ownGoal = random.nextDouble() < OWN_GOAL_PROBABILITY;
        boolean penalty = !ownGoal && random.nextDouble() < PENALTY_GOAL_PROBABILITY;
        Formation scoringFormation = ownGoal ? defendingFormation : attackingFormation;
        Map<Player, Integer> scoringExpulsions = ownGoal ? Map.of() : attackingExpulsionMinutes;
        Player scorer = selectEligibleScorer(scoringFormation, scoringExpulsions, minute, random);
        return new Goal(minute, match, scorer, penalty, ownGoal);
    }

    private Player selectEligibleScorer(
            Formation formation,
            Map<Player, Integer> expulsionMinutes,
            int minute,
            Random random) {
        List<Player> allOutfieldPlayers = getOutfieldPlayers(formation.getStarters());
        List<Player> eligiblePlayers = new ArrayList<>();
        for (Player player : allOutfieldPlayers) {
            Integer expelledAtMinute = expulsionMinutes.get(player);
            boolean stillOnPitch = expelledAtMinute == null || expelledAtMinute > minute;
            if (stillOnPitch) {
                eligiblePlayers.add(player);
            }
        }
        List<Player> candidates = eligiblePlayers.isEmpty() ? allOutfieldPlayers : eligiblePlayers;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private List<PlayerParticipation> buildParticipations(
            Formation formation,
            Map<Player, Integer> expulsionMinutes) {
        List<PlayerParticipation> result = new ArrayList<>();
        for (Player starter : formation.getStarters()) {
            Integer expelledAtMinute = expulsionMinutes.get(starter);
            int minutesPlayed = expelledAtMinute == null ? MATCH_MINUTES : expelledAtMinute;
            result.add(new PlayerParticipation(starter, true, minutesPlayed));
        }
        for (Player substitute : formation.getSubstitutes()) {
            result.add(new PlayerParticipation(substitute, false, 0));
        }
        return result;
    }

    private List<Player> getOutfieldPlayers(List<Player> players) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getPosition() != Position.GOALKEEPER) {
                result.add(player);
            }
        }
        return result;
    }
}
