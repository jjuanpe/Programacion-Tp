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
        int minute = FIRST_KICK_MINUTE;
        boolean decided = false;
        int homeTaken = 0;
        int awayTaken = 0;
        boolean homeTurn = true;

        while (!decided) {
            List<Player> kickers = homeTurn ? homeKickers : awayKickers;
            int taken = homeTurn ? homeTaken : awayTaken;
            Player kicker = kickers.get(taken % kickers.size());
            boolean converted = attemptPenalty(kicker, random);
            kicks.add(new PenaltyExecuted(minute, match, kicker, converted));
            minute = minute + 1;

            if (homeTurn) {
                homeTaken = homeTaken + 1;
                homeScored = converted ? homeScored + 1 : homeScored;
            } else {
                awayTaken = awayTaken + 1;
                awayScored = converted ? awayScored + 1 : awayScored;
            }

            decided = isShootoutOver(homeScored, awayScored, homeTaken, awayTaken);
            homeTurn = !homeTurn;
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

    private boolean isShootoutOver(int homeScored, int awayScored, int homeTaken, int awayTaken) {
        int homeKicksLeft = Math.max(0, Math.max(MINIMUM_ROUNDS, awayTaken) - homeTaken);
        int awayKicksLeft = Math.max(0, Math.max(MINIMUM_ROUNDS, homeTaken) - awayTaken);
        boolean outOfReach = homeScored > awayScored + awayKicksLeft
                || awayScored > homeScored + homeKicksLeft;
        boolean safetyLimitReached = awayTaken >= MAXIMUM_ROUNDS;
        return outOfReach || safetyLimitReached;
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
