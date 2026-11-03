package model.simulation;

import model.event.Change;
import model.event.PlayEvent;
import model.event.Expulsion;
import model.event.ExpulsionReason;
import model.event.Goal;
import model.event.Incidence;
import model.event.YellowCard;
import model.match.Formation;
import model.match.Match;
import model.match.PlayerParticipation;
import model.people.Goalkeeper;
import model.people.Player;
import model.people.Position;

import java.util.*;

public class GroupMatchSimulationService implements MatchSimulationStrategy<Match> {
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
            Match match,
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

        Set<Player> suspended;
        if (suspendedPlayers == null) {
            suspended = Set.of();
        } else {
            suspended = Set.copyOf(suspendedPlayers);
        }
        Formation homeFormation = formationService.createFormation(match.getHomeTeam(), suspended);
        Formation awayFormation = formationService.createFormation(match.getAwayTeam(), suspended);

        TeamEventPlan homePlan = createEventPlan(match, homeFormation, random);
        TeamEventPlan awayPlan = createEventPlan(match, awayFormation, random);

        double homeStrength = strengthCalculator.calculate(match.getHomeTeam(), homeFormation);
        double awayStrength = strengthCalculator.calculate(match.getAwayTeam(), awayFormation);
        int totalGoals = 0;
        while (random.nextDouble() < 0.65) {
            totalGoals++;
        }
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
            TeamEventPlan attackingPlan;
            TeamEventPlan defendingPlan;
            if (homeScores) {
                attackingPlan = homePlan;
                defendingPlan = awayPlan;
            } else {
                attackingPlan = awayPlan;
                defendingPlan = homePlan;
            }
            incidences.add(createGoal(match, attackingPlan, defendingPlan, random));
            if (homeScores) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }

        addPlayEvents(match, homePlan, random, incidences);
        addPlayEvents(match, awayPlan, random, incidences);
        incidences.sort(Comparator.comparingInt(Incidence::getMinute));
        Expulsion fifthRed = match.findFifthExpulsion(incidences);
        int endMinute = MATCH_MINUTES;
        if (fifthRed != null) {
            endMinute = fifthRed.getMinute();
            // Stop at the fifth red, including when later events share its minute.
            incidences = new ArrayList<>(incidences.subList(0, incidences.indexOf(fifthRed) + 1));
            boolean homeLost = match.getHomeTeam().getPlayers().contains(fifthRed.getPlayer());
            if (homeLost) {
                homeGoals = 0;
                awayGoals = 3;
            } else {
                homeGoals = 3;
                awayGoals = 0;
            }
            homePlan.retainEvents(incidences);
            awayPlan.retainEvents(incidences);
        }
        List<PlayerParticipation> participations = new ArrayList<>();
        participations.addAll(homePlan.getParticipations(endMinute));
        participations.addAll(awayPlan.getParticipations(endMinute));

        return new MatchSimulationResult(
                homeGoals,
                awayGoals,
                homeFormation,
                awayFormation,
                participations,
                incidences);
    }

    private void addPlayEvents(Match match, TeamEventPlan plan, Random random,
            List<Incidence> incidences) {
        int fouls = 3 + random.nextInt(10);
        for (int index = 0; index < fouls; index++) {
            int minute = 1 + random.nextInt(MATCH_MINUTES);
            incidences.add(new PlayEvent(minute, match,
                    plan.selectActivePlayer(minute, random, true), PlayEvent.FOUL));
        }
        int attempts = 2 + random.nextInt(9);
        for (int index = 0; index < attempts; index++) {
            int minute = 1 + random.nextInt(MATCH_MINUTES);
            double outcome = random.nextDouble();
            String type;
            if (outcome < 0.15) {
                type = PlayEvent.WOODWORK;
            } else if (outcome < 0.60) {
                type = PlayEvent.SHOT_SAVED;
            } else {
                type = PlayEvent.SHOT_OFF_TARGET;
            }
            incidences.add(new PlayEvent(minute, match,
                    plan.selectActivePlayer(minute, random, true), type));
        }
    }

    private double calculateHomeGoalShare(double homeStrength, double awayStrength, Random random) {
        double totalStrength = homeStrength + awayStrength;
        double strengthShare;
        if (totalStrength == 0) {
            strengthShare = 0.50;
        } else {
            strengthShare = homeStrength / totalStrength;
        }
        double surprise = random.nextDouble() * SURPRISE_VARIATION - SURPRISE_VARIATION / 2.0;
        double result = strengthShare + HOME_ADVANTAGE + surprise;
        return Math.max(MINIMUM_GOAL_SHARE, Math.min(MAXIMUM_GOAL_SHARE, result));
    }

    private TeamEventPlan createEventPlan(Match match, Formation formation, Random random) {
        List<Expulsion> expulsions = new ArrayList<>();
        List<Player> expulsionCandidates = getOutfieldPlayers(formation.getStarters());
        int attempt = 0;
        while (attempt < 5 && !expulsionCandidates.isEmpty()) {
            if (random.nextDouble() < EXPULSION_PROBABILITY) {
                Player player = selectRandom(expulsionCandidates, random);
                expulsionCandidates.remove(player);
                expulsions.add(new Expulsion(25 + random.nextInt(64), match, player));
            }
            attempt++;
        }

        List<Player> outgoingCandidates = getOutfieldPlayers(formation.getStarters());
        for (Expulsion expulsion : expulsions) {
            outgoingCandidates.remove(expulsion.getPlayer());
        }
        List<Player> substituteCandidates = new ArrayList<>(formation.getSubstitutes());
        Collections.shuffle(outgoingCandidates, random);
        Collections.shuffle(substituteCandidates, random);

        int requestedChanges;
        if (substituteCandidates.isEmpty()) {
            requestedChanges = 0;
        } else {
            requestedChanges = 1 + random.nextInt(MAX_CHANGES_PER_TEAM);
        }
        List<Change> changes = new ArrayList<>();
        int candidateIndex = 0;
        while (candidateIndex < outgoingCandidates.size()
                && changes.size() < requestedChanges) {
            Player outgoingPlayer = outgoingCandidates.get(candidateIndex);
            Player incomingPlayer = findSubstituteForPosition(
                    outgoingPlayer.getPosition(), substituteCandidates);
            if (incomingPlayer != null) {
                int minute = 55 + random.nextInt(31);
                changes.add(new Change(minute, match, outgoingPlayer, incomingPlayer));
                substituteCandidates.remove(incomingPlayer);
            }
            candidateIndex++;
        }

        return new TeamEventPlan(formation, changes, expulsions);
    }

    private Player findSubstituteForPosition(Position position, List<Player> substitutes) {
        Player selectedSubstitute = null;
        int substituteIndex = 0;
        while (substituteIndex < substitutes.size() && selectedSubstitute == null) {
            Player substitute = substitutes.get(substituteIndex);
            if (substitute.getPosition() == position) {
                selectedSubstitute = substitute;
            }
            substituteIndex++;
        }
        return selectedSubstitute;
    }

    private void addYellowCards(
            Match match,
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
        int cardIndex = 0;
        while (cardIndex < cardMinutes.size() && plan.expulsions.size() < 5) {
            int minute = cardMinutes.get(cardIndex);
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
            cardIndex++;
        }
    }

    private Goal createGoal(
            Match match,
            TeamEventPlan attackingPlan,
            TeamEventPlan defendingPlan,
            Random random) {
        int minute = 1 + random.nextInt(MATCH_MINUTES);
        boolean ownGoal = random.nextDouble() < OWN_GOAL_PROBABILITY;
        boolean penalty = !ownGoal && random.nextDouble() < PENALTY_GOAL_PROBABILITY;
        Player scorer;
        if (ownGoal) {
            scorer = defendingPlan.selectActivePlayer(minute, random, true);
        } else {
            scorer = attackingPlan.selectActivePlayer(minute, random, true);
        }
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
            boolean shouldRegisterExpulsion = true;
            int index = expulsions.size() - 1;
            while (index >= 0 && shouldRegisterExpulsion) {
                Expulsion currentExpulsion = expulsions.get(index);
                if (currentExpulsion.getPlayer() == newExpulsion.getPlayer()) {
                    if (currentExpulsion.getMinute() <= newExpulsion.getMinute()) {
                        shouldRegisterExpulsion = false;
                    } else {
                        expulsions.remove(index);
                    }
                }
                index--;
            }
            if (shouldRegisterExpulsion) {
                expulsions.add(newExpulsion);
                changes.removeIf(change -> change.getPlayerOut() == newExpulsion.getPlayer()
                        && change.getMinute() >= newExpulsion.getMinute());
            }
        }

        private void retainEvents(List<Incidence> retained) {
            changes.removeIf(change -> !retained.contains(change));
            expulsions.removeIf(expulsion -> !retained.contains(expulsion));
        }

        private List<PlayerParticipation> getParticipations(int endMinute) {
            Map<Player, Integer> entryMinuteByPlayer = new LinkedHashMap<>();
            Map<Player, Integer> exitMinuteByPlayer = new LinkedHashMap<>();
            for (Player starter : formation.getStarters()) {
                entryMinuteByPlayer.put(starter, 0);
                exitMinuteByPlayer.put(starter, endMinute);
            }
            for (Player substitute : formation.getSubstitutes()) {
                entryMinuteByPlayer.put(substitute, endMinute);
                exitMinuteByPlayer.put(substitute, endMinute);
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
            Goalkeeper selectedGoalkeeper = null;
            int playerIndex = 0;
            List<Player> starters = formation.getStarters();
            while (playerIndex < starters.size() && selectedGoalkeeper == null) {
                Player player = starters.get(playerIndex);
                if (player instanceof Goalkeeper goalkeeper) {
                    selectedGoalkeeper = goalkeeper;
                }
                playerIndex++;
            }
            if (selectedGoalkeeper == null) {
                throw new IllegalStateException("The formation does not contain a goalkeeper");
            }
            return selectedGoalkeeper;
        }
    }
}
