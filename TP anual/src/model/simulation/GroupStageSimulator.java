package model.simulation;

import model.competition.StandingEntry;
import model.competition.StandingsService;
import model.competition.Zone;
import model.event.Expulsion;
import model.event.Incidence;
import model.match.GroupMatch;
import model.people.Player;
import model.people.Referee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupStageSimulator {
    private static final int EXPECTED_ZONE_COUNT = 4;
    private static final int EXPECTED_MATCHES_PER_ZONE = 6;
    private static final int MAX_CONCURRENT_MATCHES = 8;

    private final MatchSimulationStrategy<GroupMatch> simulationStrategy;
    private final StandingsService standingsService;
    private final RefereeAssignmentService refereeAssignmentService;

    public GroupStageSimulator() {
        this(
                new GroupMatchSimulationService(),
                new StandingsService(),
                new RefereeAssignmentService());
    }

    public GroupStageSimulator(
            MatchSimulationStrategy<GroupMatch> simulationStrategy,
            StandingsService standingsService,
            RefereeAssignmentService refereeAssignmentService) {
        this.simulationStrategy = Objects.requireNonNull(simulationStrategy);
        this.standingsService = Objects.requireNonNull(standingsService);
        this.refereeAssignmentService = Objects.requireNonNull(refereeAssignmentService);
    }

    public List<MatchSimulationReport> simulate(
            List<Zone> zones,
            List<Referee> referees,
            long seed) throws InterruptedException {
        validateGroupStage(zones, referees);
        Map<LocalDate, List<ScheduledGroupMatch>> matchesByDate = groupMatchesByDate(zones);
        Random seedGenerator = new Random(seed);
        Set<Player> suspendedPlayers = Set.of();
        List<MatchSimulationReport> reports = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(
                MAX_CONCURRENT_MATCHES,
                new GroupMatchThreadFactory());
        try {
            for (List<ScheduledGroupMatch> roundMatches : matchesByDate.values()) {
                assignReferees(roundMatches, referees, seedGenerator);

                List<GroupMatchSimulationTask> tasks = new ArrayList<>();
                List<Future<?>> futures = new ArrayList<>();
                for (ScheduledGroupMatch scheduledMatch : roundMatches) {
                    GroupMatchSimulationTask task = new GroupMatchSimulationTask(
                            scheduledMatch.getMatch(),
                            suspendedPlayers,
                            simulationStrategy,
                            seedGenerator.nextLong());
                    tasks.add(task);
                    futures.add(executor.submit(task));
                }

                waitForRound(futures);
                List<MatchSimulationResult> results = new ArrayList<>();
                for (GroupMatchSimulationTask task : tasks) {
                    results.add(task.getResult());
                }

                Set<Player> nextRoundSuspensions = new HashSet<>();
                for (int index = 0; index < roundMatches.size(); index++) {
                    ScheduledGroupMatch scheduledMatch = roundMatches.get(index);
                    GroupMatch match = scheduledMatch.getMatch();
                    MatchSimulationResult result = results.get(index);
                    List<StandingEntry> standingsBefore =
                            standingsService.computeStandings(scheduledMatch.getZone());

                    match.completeMatch(
                            result.getHomeGoals(),
                            result.getAwayGoals(),
                            result.getHomeFormation(),
                            result.getAwayFormation(),
                            result.getParticipations(),
                            result.getIncidences());

                    List<StandingEntry> standingsAfter =
                            standingsService.computeStandings(scheduledMatch.getZone());
                    reports.add(new MatchSimulationReport(
                            scheduledMatch.getZone().getName(),
                            match,
                            standingsBefore,
                            standingsAfter,
                            tasks.get(index).getWorkerThreadName()));
                    collectSuspensions(result, nextRoundSuspensions);
                }
                suspendedPlayers = Set.copyOf(nextRoundSuspensions);
            }
        } finally {
            executor.shutdownNow();
        }
        return List.copyOf(reports);
    }

    private void validateGroupStage(List<Zone> zones, List<Referee> referees) {
        Objects.requireNonNull(zones, "The zone list is required");
        Objects.requireNonNull(referees, "The referee list is required");
        if (zones.size() != EXPECTED_ZONE_COUNT) {
            throw new IllegalArgumentException("The group stage must contain four zones");
        }
        for (Zone zone : zones) {
            if (zone.getGroupMatches().size() != EXPECTED_MATCHES_PER_ZONE) {
                throw new IllegalArgumentException(
                        "Zone " + zone.getName() + " must contain six matches");
            }
            for (GroupMatch match : zone.getGroupMatches()) {
                if (match.isPlayed()) {
                    throw new IllegalStateException("The group stage contains an already played match");
                }
            }
        }
        if (referees.size() < MAX_CONCURRENT_MATCHES) {
            throw new IllegalArgumentException("At least eight referees are required");
        }
    }

    private Map<LocalDate, List<ScheduledGroupMatch>> groupMatchesByDate(List<Zone> zones) {
        Map<LocalDate, List<ScheduledGroupMatch>> matchesByDate = new TreeMap<>();
        for (Zone zone : zones) {
            for (GroupMatch match : zone.getGroupMatches()) {
                matchesByDate.computeIfAbsent(match.getDate(), ignored -> new ArrayList<>())
                        .add(new ScheduledGroupMatch(zone, match));
            }
        }
        return matchesByDate;
    }

    private void assignReferees(
            List<ScheduledGroupMatch> roundMatches,
            List<Referee> referees,
            Random random) {
        List<Referee> availableReferees = new ArrayList<>(referees);
        for (ScheduledGroupMatch scheduledMatch : roundMatches) {
            GroupMatch match = scheduledMatch.getMatch();
            if (match.getReferee() != null) {
                if (!availableReferees.remove(match.getReferee())) {
                    throw new IllegalStateException("A referee cannot officiate two matches on the same date");
                }
            } else {
                Referee selectedReferee = refereeAssignmentService.selectReferee(
                        match.getHomeTeam(),
                        match.getAwayTeam(),
                        availableReferees,
                        random);
                match.assignReferee(selectedReferee);
                availableReferees.remove(selectedReferee);
            }
        }
    }

    private void waitForRound(List<Future<?>> futures) throws InterruptedException {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException exception) {
                throw new IllegalStateException("A group match simulation failed", exception.getCause());
            }
        }
    }

    private void collectSuspensions(
            MatchSimulationResult result,
            Set<Player> nextRoundSuspensions) {
        for (Incidence incidence : result.getIncidences()) {
            if (incidence instanceof Expulsion expulsion) {
                nextRoundSuspensions.add(expulsion.getPlayer());
            }
        }
    }

    private static final class ScheduledGroupMatch {
        private final Zone zone;
        private final GroupMatch match;

        private ScheduledGroupMatch(Zone zone, GroupMatch match) {
            this.zone = zone;
            this.match = match;
        }

        private Zone getZone() {
            return zone;
        }

        private GroupMatch getMatch() {
            return match;
        }
    }

    private static final class GroupMatchThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "group-match-worker-" + threadNumber.getAndIncrement());
        }
    }
}
