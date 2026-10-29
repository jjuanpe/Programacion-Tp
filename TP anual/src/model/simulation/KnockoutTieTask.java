package model.simulation;

import model.competition.StadiumDrawService;
import model.event.Expulsion;
import model.event.Incidence;
import model.match.FirstLegMatch;
import model.match.Match;
import model.match.PhaseType;
import model.match.SecondLegMatch;
import model.people.Player;
import model.people.Referee;
import model.team.Team;
import model.venue.Stadium;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;

public class KnockoutTieTask implements Callable<KnockoutTieReport> {

    private final Team teamA;
    private final Team teamB;
    private final PhaseType phase;
    private final LocalDate firstLegDate;
    private final LocalDate secondLegDate;
    private final Stadium firstLegStadium;
    private final Stadium secondLegStadium;
    private final Referee firstLegReferee;
    private final Referee secondLegReferee;
    private final MatchSimulationStrategy<Match> simulationStrategy;
    private final PenaltyShootoutSimulator penaltyShootoutSimulator;
    private final long seed;
    private final Set<Player> suspendedForFirstLeg;


    public KnockoutTieTask(
            Team teamA,
            Team teamB,
            PhaseType phase,
            LocalDate firstLegDate,
            LocalDate secondLegDate,
            Stadium firstLegStadium,
            Stadium secondLegStadium,
            Referee firstLegReferee,
            Referee secondLegReferee,
            MatchSimulationStrategy<Match> simulationStrategy,
            PenaltyShootoutSimulator penaltyShootoutSimulator,
            long seed) {
        this.teamA = Objects.requireNonNull(teamA, "Team A is required");
        this.teamB = Objects.requireNonNull(teamB, "Team B is required");
        this.phase = Objects.requireNonNull(phase, "The phase is required");
        this.firstLegDate = Objects.requireNonNull(firstLegDate, "The first leg date is required");
        this.secondLegDate = Objects.requireNonNull(secondLegDate, "The second leg date is required");
        this.firstLegStadium = Objects.requireNonNull(firstLegStadium, "The first leg stadium is required");
        this.secondLegStadium = Objects.requireNonNull(secondLegStadium, "The second leg stadium is required");
        this.firstLegReferee = Objects.requireNonNull(firstLegReferee, "The first leg referee is required");
        this.secondLegReferee = Objects.requireNonNull(secondLegReferee, "The second leg referee is required");
        this.simulationStrategy = Objects.requireNonNull(simulationStrategy, "The simulation strategy is required");
        this.penaltyShootoutSimulator =
                Objects.requireNonNull(penaltyShootoutSimulator, "The penalty shoot-out simulator is required");
        this.seed = seed;
        this.suspendedForFirstLeg = Set.copyOf(suspendedForFirstLeg);
    }

    @Override
    public KnockoutTieReport call() {
        String workerThreadName = Thread.currentThread().getName();
        Random random = new Random(seed);

        FirstLegMatch firstLeg = new FirstLegMatch(
                firstLegDate, teamA, teamB, firstLegReferee, firstLegStadium, phase);
        MatchSimulationResult firstLegResult = simulationStrategy.simulate(firstLeg, suspendedForFirstLeg, random);
        firstLegResult.applyTo(firstLeg);

        Set<Player> suspendedForSecondLeg = firstLeg.getExpelledPlayers();

        SecondLegMatch secondLeg = new SecondLegMatch(
                secondLegDate, teamB, teamA, secondLegReferee, secondLegStadium, phase, firstLeg);
        MatchSimulationResult secondLegResult =
                simulationStrategy.simulate(secondLeg, suspendedForSecondLeg, random);
        result.applyTo(secondLeg);

        Team winner = secondLeg.resolveTieWinner();
        String criteria;
        if (winner != null) {
            criteria = secondLeg.isDecidedByPlainAggregate() ? "Aggregate score" : "Away goals rule";
        } else {
            PenaltyShootoutResult shootoutResult = penaltyShootoutSimulator.simulate(
                    secondLeg, secondLeg.getHomeFormation(), secondLeg.getAwayFormation(), random);
            secondLeg.registerPenaltyShootout(shootoutResult.getKicks());
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
