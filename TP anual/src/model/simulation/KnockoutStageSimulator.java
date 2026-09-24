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

        KnockoutStageContext context = startKnockoutStage(stadiums, referees, seed);

        List<KnockoutTieReport> quarterFinalReports =
                playQuarterFinals(zones, context, firstMatchDate);

        LocalDate semiFinalDate = firstMatchDate.plusDays(DAYS_BETWEEN_STAGES);
        List<KnockoutTieReport> semiFinalReports =
                playSemiFinals(quarterFinalReports, context, semiFinalDate);

        LocalDate finalDate = semiFinalDate.plusDays(DAYS_BETWEEN_STAGES);
        FinalMatchReport finalReport = playFinalMatch(semiFinalReports, context, finalDate);

        return new KnockoutStageResult(quarterFinalReports, semiFinalReports, finalReport);
    }

    /**
     * Arranca una eliminatoria que se va a jugar fase por fase (cuartos,
     * semis y final en llamadas separadas, por ejemplo una por cada click
     * del usuario en la interfaz). Devuelve un {@link KnockoutStageContext}
     * que hay que guardar y volver a pasar en cada llamada siguiente --
     * mantiene el sorteo de estadios y el generador de semillas compartidos
     * entre las 3 fases, para que ningun estadio se repita en TODA la
     * eliminatoria (no solo dentro de una fase) y la simulacion completa
     * siga siendo reproducible con la semilla original.
     */
    public KnockoutStageContext startKnockoutStage(
            List<Stadium> stadiums, List<Referee> referees, long seed) {
        Objects.requireNonNull(stadiums, "The stadium list is required");
        Objects.requireNonNull(referees, "The referee list is required");
        return new KnockoutStageContext(
                new StadiumDrawService(stadiums), List.copyOf(referees), new Random(seed));
    }

    /** Juega solo los 4 cruces de cuartos de final. */
    public List<KnockoutTieReport> playQuarterFinals(
            List<Zone> zones, KnockoutStageContext context, LocalDate firstMatchDate)
            throws InterruptedException {
        Objects.requireNonNull(zones, "The zone list is required");
        Objects.requireNonNull(context, "The knockout stage context is required");
        Objects.requireNonNull(firstMatchDate, "The first match date is required");

        List<Team[]> quarterFinalPairings = bracketService.buildQuarterFinalPairings(zones);
        return playRound(
                quarterFinalPairings,
                PhaseType.QUARTER_FINAL,
                firstMatchDate,
                context.getStadiumDrawService(),
                context.getRefereePool(),
                context.getSeedGenerator(),
                QUARTER_FINAL_COUNT);
    }

    /** Juega solo los 2 cruces de semifinal, con los 4 ganadores de cuartos. */
    public List<KnockoutTieReport> playSemiFinals(
            List<KnockoutTieReport> quarterFinalReports,
            KnockoutStageContext context,
            LocalDate semiFinalDate) throws InterruptedException {
        Objects.requireNonNull(quarterFinalReports, "The quarter-final reports are required");
        Objects.requireNonNull(context, "The knockout stage context is required");
        Objects.requireNonNull(semiFinalDate, "The semi-final date is required");
        if (quarterFinalReports.size() != QUARTER_FINAL_COUNT) {
            throw new IllegalArgumentException("Expected the four quarter-final reports");
        }

        List<Team[]> semiFinalPairings = new ArrayList<>();
        semiFinalPairings.add(new Team[]{
                quarterFinalReports.get(0).getWinner(), quarterFinalReports.get(1).getWinner()});
        semiFinalPairings.add(new Team[]{
                quarterFinalReports.get(2).getWinner(), quarterFinalReports.get(3).getWinner()});

        return playRound(
                semiFinalPairings,
                PhaseType.SEMI_FINAL,
                semiFinalDate,
                context.getStadiumDrawService(),
                context.getRefereePool(),
                context.getSeedGenerator(),
                SEMI_FINAL_COUNT);
    }

    /** Juega solo el partido final, con los 2 ganadores de semifinal. */
    public FinalMatchReport playFinalMatch(
            List<KnockoutTieReport> semiFinalReports,
            KnockoutStageContext context,
            LocalDate finalDate) {
        Objects.requireNonNull(semiFinalReports, "The semi-final reports are required");
        Objects.requireNonNull(context, "The knockout stage context is required");
        Objects.requireNonNull(finalDate, "The final date is required");
        if (semiFinalReports.size() != SEMI_FINAL_COUNT) {
            throw new IllegalArgumentException("Expected the two semi-final reports");
        }

        return playFinal(
                semiFinalReports.get(0).getWinner(),
                semiFinalReports.get(1).getWinner(),
                finalDate,
                context.getStadiumDrawService(),
                context.getRefereePool(),
                context.getSeedGenerator());
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
