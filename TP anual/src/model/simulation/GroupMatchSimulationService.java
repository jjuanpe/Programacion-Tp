package model.simulation;

import model.event.Change;
import model.event.Expulsion;
import model.event.ExpulsionReason;
import model.event.Goal;
import model.event.Incidence;
import model.event.YellowCard;
import model.match.Formation;
import model.match.GroupMatch;
import model.match.PlayerParticipation;
import model.people.Goalkeeper;
import model.people.Player;
import model.people.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class GroupMatchSimulationService implements MatchSimulationStrategy<GroupMatch> {
    private static final int MATCH_MINUTES = 90;
    private static final int MAX_CHANGES_PER_TEAM = 3;
    private static final int MAX_YELLOW_CARDS_PER_TEAM = 3;
    private static final double EXPULSION_PROBABILITY = 0.10;
    private static final double PENALTY_GOAL_PROBABILITY = 0.15;
    private static final double OWN_GOAL_PROBABILITY = 0.05;
    private static final double HOME_ADVANTAGE = 0.05;
    private static final double SURPRISE_VARIATION = 0.30;
    private static final double MINIMUM_GOAL_SHARE = 0.20;
    private static final double MAXIMUM_GOAL_SHARE = 0.80;

    private final FormationService formationService;
    private final TeamStrengthCalculator strengthCalculator;

    public GroupMatchSimulationService() {
        this(new FormationService(), new TeamStrengthCalculator());
    }

    public GroupMatchSimulationService(
            FormationService formationService,
            TeamStrengthCalculator strengthCalculator) {
        this.formationService = Objects.requireNonNull(formationService);
        this.strengthCalculator = Objects.requireNonNull(strengthCalculator);
    }

    @Override
    public MatchSimulationResult simulate(
            GroupMatch match,
            Set<Player> suspendedPlayers,
            Random random) {
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

        TeamEventPlan homePlan = createEventPlan(match, homeFormation, random);
        TeamEventPlan awayPlan = createEventPlan(match, awayFormation, random);

        double homeStrength = strengthCalculator.calculate(match.getHomeTeam(), homeFormation);
        double awayStrength = strengthCalculator.calculate(match.getAwayTeam(), awayFormation);
        int totalGoals = random.nextInt(3) + random.nextInt(3);
        double homeGoalShare = calculateHomeGoalShare(homeStrength, awayStrength, random);

        int homeGoals = 0;
        int awayGoals = 0;
        List<Incidence> incidences = new ArrayList<>();
        addYellowCards(match, homePlan, random, incidences);
        addYellowCards(match, awayPlan, random, incidences);
        incidences.addAll(homePlan.getBaseIncidences());
        incidences.addAll(awayPlan.getBaseIncidences());

        for (int goalNumber = 0; goalNumber < totalGoals; goalNumber++) {
            boolean homeScores = random.nextDouble() < homeGoalShare;
            TeamEventPlan attackingPlan = homeScores ? homePlan : awayPlan;
            TeamEventPlan defendingPlan = homeScores ? awayPlan : homePlan;
            incidences.add(createGoal(match, attackingPlan, defendingPlan, random));
            if (homeScores) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }

        incidences.sort(Comparator.comparingInt(Incidence::getMinute));
        List<PlayerParticipation> participations = new ArrayList<>();
        participations.addAll(homePlan.getParticipations());
        participations.addAll(awayPlan.getParticipations());

        return new MatchSimulationResult(
                homeGoals,
                awayGoals,
                homeFormation,
                awayFormation,
                participations,
                incidences);
    }

    private double calculateHomeGoalShare(double homeStrength, double awayStrength, Random random) {
        double totalStrength = homeStrength + awayStrength;
        double strengthShare = totalStrength == 0 ? 0.50 : homeStrength / totalStrength;
        double surprise = random.nextDouble() * SURPRISE_VARIATION - SURPRISE_VARIATION / 2.0;
        double result = strengthShare + HOME_ADVANTAGE + surprise;
        return Math.max(MINIMUM_GOAL_SHARE, Math.min(MAXIMUM_GOAL_SHARE, result));
    }

    private TeamEventPlan createEventPlan(GroupMatch match, Formation formation, Random random) {
        Player expelledPlayer = null;
        List<Expulsion> expulsions = new ArrayList<>();
        if (random.nextDouble() < EXPULSION_PROBABILITY) {
            List<Player> expulsionCandidates = getOutfieldPlayers(formation.getStarters());
            expelledPlayer = selectRandom(expulsionCandidates, random);
            int minute = 25 + random.nextInt(64);
            expulsions.add(new Expulsion(minute, match, expelledPlayer));
        }

        List<Player> outgoingCandidates = getOutfieldPlayers(formation.getStarters());
        outgoingCandidates.remove(expelledPlayer);
        List<Player> substituteCandidates = new ArrayList<>(formation.getSubstitutes());
        Collections.shuffle(outgoingCandidates, random);
        Collections.shuffle(substituteCandidates, random);

        int requestedChanges = substituteCandidates.isEmpty() ? 0 : 1 + random.nextInt(MAX_CHANGES_PER_TEAM);
        List<Change> changes = new ArrayList<>();
        for (Player outgoingPlayer : outgoingCandidates) {
            if (changes.size() == requestedChanges) {
                break;
            }
            Player incomingPlayer = findSubstituteForPosition(
                    outgoingPlayer.getPosition(), substituteCandidates);
            if (incomingPlayer != null) {
                int minute = 55 + random.nextInt(31);
                changes.add(new Change(minute, match, outgoingPlayer, incomingPlayer));
                substituteCandidates.remove(incomingPlayer);
            }
        }

        return new TeamEventPlan(formation, changes, expulsions);
    }

    private Player findSubstituteForPosition(Position position, List<Player> substitutes) {
        for (Player substitute : substitutes) {
            if (substitute.getPosition() == position) {
                return substitute;
            }
        }
        return null;
    }

    private void addYellowCards(
            GroupMatch match,
            TeamEventPlan plan,
            Random random,
            List<Incidence> incidences) {
        int yellowCardCount = random.nextInt(MAX_YELLOW_CARDS_PER_TEAM + 1);
        List<Integer> cardMinutes = new ArrayList<>();
        for (int index = 0; index < yellowCardCount; index++) {
            cardMinutes.add(1 + random.nextInt(MATCH_MINUTES - 1));
        }
        cardMinutes.sort(Integer::compareTo);

        Map<Player, Integer> yellowCardsByPlayer = new HashMap<>();
        for (int minute : cardMinutes) {
            Player player = plan.selectActivePlayer(minute, random, true);
            incidences.add(new YellowCard(minute, match, player));
            int playerYellowCards = yellowCardsByPlayer.merge(player, 1, Integer::sum);
            if (playerYellowCards == 2) {
                plan.registerExpulsion(new Expulsion(
                        minute,
                        match,
                        player,
                        ExpulsionReason.SECOND_YELLOW_CARD));
            }
        }
    }

    private Goal createGoal(
            GroupMatch match,
            TeamEventPlan attackingPlan,
            TeamEventPlan defendingPlan,
            Random random) {
        int minute = 1 + random.nextInt(MATCH_MINUTES);
        boolean ownGoal = random.nextDouble() < OWN_GOAL_PROBABILITY;
        boolean penalty = !ownGoal && random.nextDouble() < PENALTY_GOAL_PROBABILITY;
        Player scorer = ownGoal
                ? defendingPlan.selectActivePlayer(minute, random, true)
                : attackingPlan.selectActivePlayer(minute, random, true);
        Goalkeeper goalkeeper = defendingPlan.getGoalkeeper();
        return new Goal(minute, match, scorer, goalkeeper, penalty, ownGoal);
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

    private Player selectRandom(List<Player> players, Random random) {
        if (players.isEmpty()) {
            throw new IllegalStateException("There are no players available for this incidence");
        }
        return players.get(random.nextInt(players.size()));
    }

    private static final class TeamEventPlan {
        private final Formation formation;
        private final List<Change> changes;
        private final List<Expulsion> expulsions;

        private TeamEventPlan(
                Formation formation,
                List<Change> changes,
                List<Expulsion> expulsions) {
            this.formation = formation;
            this.changes = new ArrayList<>(changes);
            this.expulsions = new ArrayList<>(expulsions);
        }

        private List<Incidence> getBaseIncidences() {
            List<Incidence> result = new ArrayList<>(changes);
            result.addAll(expulsions);
            return result;
        }

        private void registerExpulsion(Expulsion newExpulsion) {
            for (int index = expulsions.size() - 1; index >= 0; index--) {
                Expulsion currentExpulsion = expulsions.get(index);
                if (currentExpulsion.getPlayer() == newExpulsion.getPlayer()) {
                    if (currentExpulsion.getMinute() <= newExpulsion.getMinute()) {
                        return;
                    }
                    expulsions.remove(index);
                }
            }
            expulsions.add(newExpulsion);
            changes.removeIf(change -> change.getPlayerOut() == newExpulsion.getPlayer()
                    && change.getMinute() >= newExpulsion.getMinute());
        }

        private List<PlayerParticipation> getParticipations() {
            Map<Player, Integer> entryMinuteByPlayer = new LinkedHashMap<>();
            Map<Player, Integer> exitMinuteByPlayer = new LinkedHashMap<>();
            for (Player starter : formation.getStarters()) {
                entryMinuteByPlayer.put(starter, 0);
                exitMinuteByPlayer.put(starter, MATCH_MINUTES);
            }
            for (Player substitute : formation.getSubstitutes()) {
                entryMinuteByPlayer.put(substitute, MATCH_MINUTES);
                exitMinuteByPlayer.put(substitute, MATCH_MINUTES);
            }
            for (Change change : changes) {
                exitMinuteByPlayer.put(
                        change.getPlayerOut(),
                        Math.min(exitMinuteByPlayer.get(change.getPlayerOut()), change.getMinute()));
                entryMinuteByPlayer.put(
                        change.getPlayerIn(),
                        Math.min(entryMinuteByPlayer.get(change.getPlayerIn()), change.getMinute()));
            }
            for (Expulsion expulsion : expulsions) {
                exitMinuteByPlayer.put(
                        expulsion.getPlayer(),
                        Math.min(exitMinuteByPlayer.get(expulsion.getPlayer()), expulsion.getMinute()));
            }

            List<PlayerParticipation> result = new ArrayList<>();
            for (Player player : formation.getStarters()) {
                result.add(new PlayerParticipation(
                        player,
                        true,
                        calculateMinutesPlayed(player, entryMinuteByPlayer, exitMinuteByPlayer)));
            }
            for (Player player : formation.getSubstitutes()) {
                result.add(new PlayerParticipation(
                        player,
                        false,
                        calculateMinutesPlayed(player, entryMinuteByPlayer, exitMinuteByPlayer)));
            }
            return result;
        }

        private int calculateMinutesPlayed(
                Player player,
                Map<Player, Integer> entryMinuteByPlayer,
                Map<Player, Integer> exitMinuteByPlayer) {
            return Math.max(0, exitMinuteByPlayer.get(player) - entryMinuteByPlayer.get(player));
        }

        private Player selectActivePlayer(int minute, Random random, boolean outfieldOnly) {
            List<Player> activePlayers = new ArrayList<>(formation.getStarters());
            for (Change change : changes) {
                if (change.getMinute() <= minute) {
                    activePlayers.remove(change.getPlayerOut());
                    activePlayers.add(change.getPlayerIn());
                }
            }
            for (Expulsion expulsion : expulsions) {
                if (expulsion.getMinute() <= minute) {
                    activePlayers.remove(expulsion.getPlayer());
                }
            }
            if (outfieldOnly) {
                activePlayers.removeIf(player -> player.getPosition() == Position.GOALKEEPER);
            }
            if (activePlayers.isEmpty()) {
                throw new IllegalStateException("There are no active players for this incidence");
            }
            return activePlayers.get(random.nextInt(activePlayers.size()));
        }

        private Goalkeeper getGoalkeeper() {
            for (Player player : formation.getStarters()) {
                if (player instanceof Goalkeeper goalkeeper) {
                    return goalkeeper;
                }
            }
            throw new IllegalStateException("The formation does not contain a goalkeeper");
        }
    }
}
