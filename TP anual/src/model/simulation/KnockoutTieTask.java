package model.simulation;

import model.event.Expulsion;
import model.event.Incidence;
import model.match.FirstLegMatch;
import model.match.Match;
import model.match.PhaseType;
import model.match.SecondLegMatch;
import model.people.Player;
import model.people.Referee;
import model.competition.StadiumDrawService;
import model.team.Team;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

public class KnockoutTieTask implements Callable<KnockoutTieReport> {

    private final Team teamA;
    private final Team teamB;
    private final PhaseType phase;
    private final LocalDate firstLegDate;
    private final LocalDate secondLegDate;
    private final StadiumDrawService stadiumDrawService;
    private final List<Referee> availableReferees;
    private final MatchSimulationStrategy<Match> simulationStrategy;
    private final PenaltyShootoutSimulator penaltyShootoutSimulator;
    private final RefereeAssignmentService refereeAssignmentService;
    private final long seed;

    public KnockoutTieTask(
            Team teamA,
            Team teamB,
            PhaseType phase,
            LocalDate firstLegDate,
            LocalDate secondLegDate,
            StadiumDrawService stadiumDrawService,
            List<Referee> availableReferees,
            MatchSimulationStrategy<Match> simulationStrategy,
            PenaltyShootoutSimulator penaltyShootoutSimulator,
            RefereeAssignmentService refereeAssignmentService,
            long seed) {
        this.teamA = Objects.requireNonNull(teamA, "Team A is required");
        this.teamB = Objects.requireNonNull(teamB, "Team B is required");
        this.phase = Objects.requireNonNull(phase, "The phase is required");
        this.firstLegDate = Objects.requireNonNull(firstLegDate, "The first leg date is required");
        this.secondLegDate = Objects.requireNonNull(secondLegDate, "The second leg date is required");
        this.stadiumDrawService = Objects.requireNonNull(stadiumDrawService, "The stadium draw service is required");
        this.availableReferees = List.copyOf(availableReferees);
        this.simulationStrategy = Objects.requireNonNull(simulationStrategy, "The simulation strategy is required");
        this.penaltyShootoutSimulator =
                Objects.requireNonNull(penaltyShootoutSimulator, "The penalty shoot-out simulator is required");
        this.refereeAssignmentService =
                Objects.requireNonNull(refereeAssignmentService, "The referee assignment service is required");
        this.seed = seed;
    }

    @Override
    public KnockoutTieReport call() {
        String workerThreadName = Thread.currentThread().getName();
        Random random = new Random(seed);

        Referee firstLegReferee = refereeAssignmentService.selectReferee(teamA, teamB, availableReferees, random);
        FirstLegMatch firstLeg = new FirstLegMatch(
                firstLegDate, teamA, teamB, firstLegReferee, stadiumDrawService.draw(random), phase);
        MatchSimulationResult firstLegResult = simulationStrategy.simulate(firstLeg, Set.of(), random);
        firstLeg.completeMatch(
                firstLegResult.getHomeGoals(),
                firstLegResult.getAwayGoals(),
                firstLegResult.getHomeFormation(),
                firstLegResult.getAwayFormation(),
                firstLegResult.getParticipations(),
                firstLegResult.getIncidences());

        Set<Player> suspendedForSecondLeg = collectExpelledPlayers(firstLegResult.getIncidences());

        Referee secondLegReferee = refereeAssignmentService.selectReferee(teamB, teamA, availableReferees, random);
        SecondLegMatch secondLeg = new SecondLegMatch(
                secondLegDate, teamB, teamA, secondLegReferee, stadiumDrawService.draw(random), phase, firstLeg);
        MatchSimulationResult secondLegResult =
                simulationStrategy.simulate(secondLeg, suspendedForSecondLeg, random);
        secondLeg.completeMatch(
                secondLegResult.getHomeGoals(),
                secondLegResult.getAwayGoals(),
                secondLegResult.getHomeFormation(),
                secondLegResult.getAwayFormation(),
                secondLegResult.getParticipations(),
                secondLegResult.getIncidences());

        Team winner = secondLeg.resolveTieWinner();
        String criteria;
        if (winner != null) {
            criteria = secondLeg.isDecidedByPlainAggregate() ? "Aggregate score" : "Away goals rule";
        } else {
            PenaltyShootoutResult shootoutResult = penaltyShootoutSimulator.simulate(
                    secondLeg, secondLeg.getHomeFormation(), secondLeg.getAwayFormation(), random);
            winner = shootoutResult.getWinner();
            criteria = "Penalty shoot-out";
        }

        return new KnockoutTieReport(phase, firstLeg, secondLeg, winner, criteria, workerThreadName);
    }

    private Set<Player> collectExpelledPlayers(List<Incidence> incidences) {
        Set<Player> expelledPlayers = new HashSet<>();
        for (Incidence incidence : incidences) {
            if (incidence instanceof Expulsion expulsion) {
                expelledPlayers.add(expulsion.getPlayer());
            }
        }
        return expelledPlayers;
    }
}
