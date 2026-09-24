package model.simulation;

import model.event.PenaltyExecuted;
import model.match.Formation;
import model.match.Match;
import model.people.Player;
import model.people.Position;
import model.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class PenaltyShootoutSimulator {

    private static final int MINIMUM_ROUNDS = 5;
    private static final int MAXIMUM_ROUNDS = 15;
    private static final int FIRST_KICK_MINUTE = 121;
    private static final double BASE_SCORE_PROBABILITY = 0.72;
    private static final double SKILL_PROBABILITY_DIVISOR = 400.0;
    private static final double MINIMUM_SCORE_PROBABILITY = 0.50;
    private static final double MAXIMUM_SCORE_PROBABILITY = 0.95;

    public PenaltyShootoutResult simulate(
            Match match,
            Formation homeFormation,
            Formation awayFormation,
            Random random) {
        Objects.requireNonNull(match, "The match is required");
        Objects.requireNonNull(homeFormation, "The home formation is required");
        Objects.requireNonNull(awayFormation, "The away formation is required");
        Objects.requireNonNull(random, "The random generator is required");

        List<Player> homeKickers = buildKickerOrder(homeFormation, random);
        List<Player> awayKickers = buildKickerOrder(awayFormation, random);

        List<PenaltyExecuted> kicks = new ArrayList<>();
        int homeScored = 0;
        int awayScored = 0;
        int round = 0;
        int minute = FIRST_KICK_MINUTE;
        boolean decided = false;

        while (!decided) {
            round = round + 1;

            Player homeKicker = homeKickers.get((round - 1) % homeKickers.size());
            boolean homeConverted = attemptPenalty(homeKicker, random);
            kicks.add(new PenaltyExecuted(minute, match, homeKicker, homeConverted));
            minute = minute + 1;
            homeScored = homeConverted ? homeScored + 1 : homeScored;

            Player awayKicker = awayKickers.get((round - 1) % awayKickers.size());
            boolean awayConverted = attemptPenalty(awayKicker, random);
            kicks.add(new PenaltyExecuted(minute, match, awayKicker, awayConverted));
            minute = minute + 1;
            awayScored = awayConverted ? awayScored + 1 : awayScored;

            boolean regulationRoundsDone = round >= MINIMUM_ROUNDS;
            boolean scoresDiffer = homeScored != awayScored;
            boolean safetyLimitReached = round >= MAXIMUM_ROUNDS;
            decided = (regulationRoundsDone && scoresDiffer) || safetyLimitReached;
        }

        Team winner = resolveWinner(match, homeScored, awayScored, random);
        return new PenaltyShootoutResult(winner, kicks);
    }

    private Team resolveWinner(Match match, int homeScored, int awayScored, Random random) {
        Team winner;
        if (homeScored > awayScored) {
            winner = match.getHomeTeam();
        } else if (awayScored > homeScored) {
            winner = match.getAwayTeam();
        } else {
            winner = random.nextBoolean() ? match.getHomeTeam() : match.getAwayTeam();
        }
        return winner;
    }

    private boolean attemptPenalty(Player kicker, Random random) {
        double probability = BASE_SCORE_PROBABILITY + kicker.getAverage() / SKILL_PROBABILITY_DIVISOR;
        double clampedProbability = Math.max(
                MINIMUM_SCORE_PROBABILITY, Math.min(MAXIMUM_SCORE_PROBABILITY, probability));
        return random.nextDouble() < clampedProbability;
    }

    private List<Player> buildKickerOrder(Formation formation, Random random) {
        List<Player> outfieldPlayers = new ArrayList<>();
        for (Player player : formation.getStarters()) {
            if (player.getPosition() != Position.GOALKEEPER) {
                outfieldPlayers.add(player);
            }
        }
        List<Player> shuffled = new ArrayList<>(outfieldPlayers);
        Collections.shuffle(shuffled, random);
        return shuffled;
    }
}
