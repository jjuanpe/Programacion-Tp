package app;

import dataload.JsonTournamentLoader;
import dataload.TournamentData;
import model.competition.DrawService;
import model.competition.FixtureService;
import model.competition.StandingsService;
import model.competition.Zone;
import model.simulation.GroupStageSimulator;
import model.simulation.MatchSimulationReport;
import ui.ConsoleReportFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class Sprint3Main {
    private static final String DEFAULT_DATA_PATH = "docs/torneo.json";
    private static final LocalDate FIRST_ROUND_DATE = LocalDate.of(2026, 9, 1);
    private static final int DAYS_BETWEEN_ROUNDS = 7;
    private static final Random SEED_GENERATOR = new Random();

    public static void main(String[] args) throws Exception {
        String dataPath = args.length == 0 ? DEFAULT_DATA_PATH : args[0];
        long drawSeed = args.length >= 2 ? Long.parseLong(args[1]) : generateRandomSeed();
        long simulationSeed = args.length >= 3 ? Long.parseLong(args[2]) : generateRandomSeed();
        System.out.println("Draw seed: " + drawSeed);
        System.out.println("Simulation seed: " + simulationSeed);

        TournamentData tournamentData = new JsonTournamentLoader(dataPath).load();

        List<Zone> zones = new DrawService(new Random(drawSeed))
                .draw(tournamentData.getTeams());
        FixtureService fixtureService = new FixtureService();
        for (Zone zone : zones) {
            fixtureService.generateFixture(zone, FIRST_ROUND_DATE, DAYS_BETWEEN_ROUNDS);
        }

        GroupStageSimulator simulator = new GroupStageSimulator();
        List<MatchSimulationReport> reports = simulator.simulate(
                zones,
                tournamentData.getReferees(),
                simulationSeed);

        ConsoleReportFormatter formatter = new ConsoleReportFormatter();
        for (MatchSimulationReport report : reports) {
            System.out.print(formatter.format(report));
        }

        StandingsService standingsService = new StandingsService();
        for (Zone zone : zones) {
            System.out.print(formatter.formatFinalStandings(
                    zone.getName(),
                    standingsService.computeStandings(zone)));
        }
    }

    private static long generateRandomSeed() {
        return SEED_GENERATOR.nextLong();
    }
}
