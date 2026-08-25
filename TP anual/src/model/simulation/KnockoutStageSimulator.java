package model.simulation;

import model.competition.KnockoutBracketService;
import model.competition.StadiumDrawService;
import model.competition.Zone;
import model.match.FinalMatch;
import model.match.Match;
import model.match.PhaseType;
import model.people.Referee;
import model.team.Team;
import model.venue.Stadium;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs the whole knockout stage: builds the quarter-final bracket from the
 * zones' standings, plays every two-legged tie (quarter-finals and
 * semi-finals) concurrently -- one thread per tie, reusing the
 * {@code ExecutorService} pattern from Sprint 3 -- and finally plays the
 * single final match, until a champion is reached.
 */
public class KnockoutStageSimulator {

    private static final int QUARTER_FINAL_COUNT = 4;
    private static final int SEMI_FINAL_COUNT = 2;
    private static final int DAYS_BETWEEN_LEGS = 7;
    private static final int DAYS_BETWEEN_STAGES = 14;

    private final KnockoutBracketService bracketService;
    private final MatchSimulationStrategy<Match> simulationStrategy;
    private final PenaltyShootoutSimulator penaltyShootoutSimulator;
    private final RefereeAssignmentService refereeAssignmentService;

    public KnockoutStageSimulator() {
        this(
                new KnockoutBracketService(),
                new KnockoutMatchSimulationService(),
                new PenaltyShootoutSimulator(),
                new RefereeAssignmentService());
    }

    public KnockoutStageSimulator(
            KnockoutBracketService bracketService,
            MatchSimulationStrategy<Match> simulationStrategy,
            PenaltyShootoutSimulator penaltyShootoutSimulator,
            RefereeAssignmentService refereeAssignmentService) {
        this.bracketService = Objects.requireNonNull(bracketService, "The bracket service is required");
        this.simulationStrategy = Objects.requireNonNull(simulationStrategy, "The simulation strategy is required");
        this.penaltyShootoutSimulator =
                Objects.requireNonNull(penaltyShootoutSimulator, "The penalty shoot-out simulator is required");
        this.refereeAssignmentService =
                Objects.requireNonNull(refereeAssignmentService, "The referee assignment service is required");
    }

    public KnockoutStageResult simulate(
            List<Zone> zones,
            List<Stadium> stadiums,
            List<Referee> referees,
            LocalDate firstMatchDate,
            long seed) throws InterruptedException {
        Objects.requireNonNull(zones, "The zone list is required");
        Objects.requireNonNull(stadiums, "The stadium list is required");
        Objects.requireNonNull(referees, "The referee list is required");
        Objects.requireNonNull(firstMatchDate, "The first match date is required");

        List<Team[]> quarterFinalPairings = bracketService.buildQuarterFinalPairings(zones);
        StadiumDrawService stadiumDrawService = new StadiumDrawService(stadiums);
        List<Referee> refereePool = List.copyOf(referees);
        Random seedGenerator = new Random(seed);

        List<KnockoutTieReport> quarterFinalReports = playRound(
                quarterFinalPairings,
                PhaseType.QUARTER_FINAL,
                firstMatchDate,
                stadiumDrawService,
                refereePool,
                seedGenerator,
                QUARTER_FINAL_COUNT);

        List<Team[]> semiFinalPairings = new ArrayList<>();
        semiFinalPairings.add(new Team[]{
                quarterFinalReports.get(0).getWinner(), quarterFinalReports.get(1).getWinner()});
        semiFinalPairings.add(new Team[]{
                quarterFinalReports.get(2).getWinner(), quarterFinalReports.get(3).getWinner()});

        LocalDate semiFinalDate = firstMatchDate.plusDays(DAYS_BETWEEN_STAGES);
        List<KnockoutTieReport> semiFinalReports = playRound(
                semiFinalPairings,
                PhaseType.SEMI_FINAL,
                semiFinalDate,
                stadiumDrawService,
                refereePool,
                seedGenerator,
                SEMI_FINAL_COUNT);

        LocalDate finalDate = semiFinalDate.plusDays(DAYS_BETWEEN_STAGES);
        FinalMatchReport finalReport = playFinal(
                semiFinalReports.get(0).getWinner(),
                semiFinalReports.get(1).getWinner(),
                finalDate,
                stadiumDrawService,
                refereePool,
                seedGenerator);

        return new KnockoutStageResult(quarterFinalReports, semiFinalReports, finalReport);
    }

    private List<KnockoutTieReport> playRound(
            List<Team[]> pairings,
            PhaseType phase,
            LocalDate firstLegDate,
            StadiumDrawService stadiumDrawService,
            List<Referee> refereePool,
            Random seedGenerator,
            int expectedTieCount) throws InterruptedException {
        if (pairings.size() != expectedTieCount) {
            throw new IllegalArgumentException("Expected " + expectedTieCount + " ties for phase " + phase);
        }

        LocalDate secondLegDate = firstLegDate.plusDays(DAYS_BETWEEN_LEGS);
        ExecutorService executor = Executors.newFixedThreadPool(pairings.size(), new KnockoutThreadFactory());
        List<Future<KnockoutTieReport>> futures = new ArrayList<>();
        List<KnockoutTieReport> reports;
        try {
            for (Team[] pairing : pairings) {
                KnockoutTieTask task = new KnockoutTieTask(
                        pairing[0],
                        pairing[1],
                        phase,
                        firstLegDate,
                        secondLegDate,
                        stadiumDrawService,
                        refereePool,
                        simulationStrategy,
                        penaltyShootoutSimulator,
                        refereeAssignmentService,
                        seedGenerator.nextLong());
                futures.add(executor.submit(task));
            }
            reports = collectResults(futures);
        } finally {
            executor.shutdownNow();
        }
        return reports;
    }

    private List<KnockoutTieReport> collectResults(
            List<Future<KnockoutTieReport>> futures) throws InterruptedException {
        List<KnockoutTieReport> reports = new ArrayList<>();
        for (Future<KnockoutTieReport> future : futures) {
            reports.add(getResult(future));
        }
        return reports;
    }

    private KnockoutTieReport getResult(Future<KnockoutTieReport> future) throws InterruptedException {
        KnockoutTieReport report;
        try {
            report = future.get();
        } catch (ExecutionException exception) {
            throw new IllegalStateException("A knockout tie simulation failed", exception.getCause());
        }
        return report;
    }

    private FinalMatchReport playFinal(
            Team teamA,
            Team teamB,
            LocalDate date,
            StadiumDrawService stadiumDrawService,
            List<Referee> refereePool,
            Random seedGenerator) {
        Random random = new Random(seedGenerator.nextLong());
        Referee referee = refereeAssignmentService.selectReferee(teamA, teamB, refereePool, random);
        FinalMatch finalMatch = new FinalMatch(date, teamA, teamB, referee, stadiumDrawService.draw(random));

        MatchSimulationResult result = simulationStrategy.simulate(finalMatch, Set.of(), random);
        finalMatch.completeMatch(
                result.getHomeGoals(),
                result.getAwayGoals(),
                result.getHomeFormation(),
                result.getAwayFormation(),
                result.getParticipations(),
                result.getIncidences());

        Team champion;
        String criteria;
        if (finalMatch.getHomeGoals() != finalMatch.getAwayGoals()) {
            champion = finalMatch.getHomeGoals() > finalMatch.getAwayGoals() ? teamA : teamB;
            criteria = "Regulation time";
        } else {
            PenaltyShootoutResult shootoutResult = penaltyShootoutSimulator.simulate(
                    finalMatch, finalMatch.getHomeFormation(), finalMatch.getAwayFormation(), random);
            champion = shootoutResult.getWinner();
            criteria = "Penalty shoot-out";
        }

        return new FinalMatchReport(finalMatch, teamA, teamB, champion, criteria);
    }

    private static final class KnockoutThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "knockout-tie-worker-" + threadNumber.getAndIncrement());
        }
    }
}
