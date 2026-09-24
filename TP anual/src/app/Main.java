package app;

import dataload.JsonTournamentLoader;
import dao.StadiumDAO;
import dataload.TournamentData;
import model.competition.Championship;
import model.competition.DrawService;
import model.competition.FixtureService;
import model.competition.StandingsService;
import model.competition.Zone;
import model.simulation.GroupStageSimulator;
import model.simulation.KnockoutStageResult;
import model.simulation.KnockoutStageSimulator;
import model.simulation.KnockoutTieReport;
import model.simulation.MatchSimulationReport;
import model.team.Team;
import model.venue.Stadium;
import ui.ConsoleReportFormatter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class Main {
    private static final String DEFAULT_DATA_PATH = "docs/torneo.json";
    private static final LocalDate GROUP_STAGE_START_DATE = LocalDate.of(2026, 9, 1);
    private static final LocalDate KNOCKOUT_STAGE_START_DATE = LocalDate.of(2026, 11, 1);
    private static final int DAYS_BETWEEN_ROUNDS = 7;
    private static final int DRAW_SEED_ARGUMENT_INDEX = 1;
    private static final int GROUP_STAGE_SEED_ARGUMENT_INDEX = 2;
    private static final int KNOCKOUT_SEED_ARGUMENT_INDEX = 3;
    private static final Random SEED_GENERATOR = new Random();

    public static void main(String[] args) {
        try {
            runTournament(args);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.err.println("The tournament simulation was interrupted.");
        } catch (IOException | SQLException | IllegalArgumentException | IllegalStateException exception) {
            System.err.println("The tournament could not be completed: " + exception.getMessage());
        }
    }

    private static void runTournament(String[] args) throws IOException, SQLException, InterruptedException {
        String dataPath = args.length == 0 ? DEFAULT_DATA_PATH : args[0];
        long drawSeed = readSeed(args, DRAW_SEED_ARGUMENT_INDEX);
        long groupStageSeed = readSeed(args, GROUP_STAGE_SEED_ARGUMENT_INDEX);
        long knockoutSeed = readSeed(args, KNOCKOUT_SEED_ARGUMENT_INDEX);
        printSeeds(drawSeed, groupStageSeed, knockoutSeed);

        TournamentData tournamentData = new JsonTournamentLoader(dataPath).load();
        printLoadedData(tournamentData);
        List<Stadium> stadiums = new StadiumDAO().getAllStadiums();
        if (stadiums.size() < 13) {
            throw new IllegalStateException("At least 13 stadiums are required for the knockout stage");
        }

        List<Zone> zones = new DrawService(new Random(drawSeed)).draw(tournamentData.getTeams());
        generateGroupStageFixtures(zones, stadiums, drawSeed);
        Championship championship = new Championship(
                tournamentData.getTeams(),
                zones,
                tournamentData.getReferees(),
                stadiums);

        ConsoleReportFormatter formatter = new ConsoleReportFormatter();
        simulateAndPrintGroupStage(championship, groupStageSeed, formatter);
        simulateAndPrintKnockoutStage(championship, knockoutSeed, formatter);
    }

    private static long readSeed(String[] args, int argumentIndex) {
        long seed;
        if (args.length > argumentIndex) {
            seed = Long.parseLong(args[argumentIndex]);
        } else {
            seed = generateRandomSeed();
        }
        return seed;
    }

    private static long generateRandomSeed() {
        return SEED_GENERATOR.nextLong();
    }

    private static void printSeeds(long drawSeed, long groupStageSeed, long knockoutSeed) {
        System.out.println("Draw seed: " + drawSeed);
        System.out.println("Group stage seed: " + groupStageSeed);
        System.out.println("Knockout stage seed: " + knockoutSeed);
    }

    private static void printLoadedData(TournamentData tournamentData) {
        System.out.println();
        System.out.println("========================================================");
        System.out.println(" TOURNAMENT DATA");
        System.out.println("========================================================");
        System.out.println("Teams: " + tournamentData.getTeams().size());
        for (Team team : tournamentData.getTeams()) {
            System.out.println(" - " + team.getName() + " (" + team.getCountry().getCountryName()
                    + ") players: " + team.getPlayers().size());
        }
        System.out.println("Referees: " + tournamentData.getReferees().size());
        System.out.println("Warnings: " + tournamentData.getWarnings().size());
        for (String warning : tournamentData.getWarnings()) {
            System.out.println(" ! " + warning);
        }
    }

    private static void generateGroupStageFixtures(List<Zone> zones, List<Stadium> stadiums, long seed) {
        FixtureService fixtureService = new FixtureService();
        Random random = new Random(seed);
        for (Zone zone : zones) {
            fixtureService.generateFixture(zone, GROUP_STAGE_START_DATE, DAYS_BETWEEN_ROUNDS,
                    stadiums, random);
        }
    }

    private static void simulateAndPrintGroupStage(
            Championship championship,
            long groupStageSeed,
            ConsoleReportFormatter formatter) throws InterruptedException {
        GroupStageSimulator groupStageSimulator = new GroupStageSimulator();
        List<MatchSimulationReport> reports = groupStageSimulator.simulate(
                championship.getZones(),
                championship.getReferees(),
                groupStageSeed);

        System.out.println();
        System.out.println("========================================================");
        System.out.println(" GROUP STAGE");
        System.out.println("========================================================");
        for (MatchSimulationReport report : reports) {
            System.out.print(formatter.format(report));
        }

        StandingsService standingsService = new StandingsService();
        for (Zone zone : championship.getZones()) {
            System.out.print(formatter.formatFinalStandings(
                    zone.getName(),
                    standingsService.computeStandings(zone)));
        }
    }

    private static void simulateAndPrintKnockoutStage(
            Championship championship,
            long knockoutSeed,
            ConsoleReportFormatter formatter) throws InterruptedException {
        KnockoutStageSimulator knockoutStageSimulator = new KnockoutStageSimulator();
        KnockoutStageResult knockoutResult = knockoutStageSimulator.simulate(
                championship.getZones(),
                championship.getStadiums(),
                championship.getReferees(),
                KNOCKOUT_STAGE_START_DATE,
                knockoutSeed);
        for (model.match.Match match : knockoutResult.getMatches()) {
            championship.consumeStadium(match.getStadium());
        }

        System.out.println();
        System.out.println("========================================================");
        System.out.println(" KNOCKOUT STAGE");
        System.out.println("========================================================");
        System.out.println();
        System.out.println("--- Quarter-finals ---");
        for (KnockoutTieReport report : knockoutResult.getQuarterFinals()) {
            System.out.print(formatter.formatKnockoutTie(report));
        }
        System.out.println("--- Semi-finals ---");
        for (KnockoutTieReport report : knockoutResult.getSemiFinals()) {
            System.out.print(formatter.formatKnockoutTie(report));
        }
        System.out.print(formatter.formatFinal(knockoutResult.getFinalMatchReport()));

        System.out.println();
        System.out.println("========================================================");
        System.out.println(" CHAMPION: " + knockoutResult.getChampion().getName());
        System.out.println("========================================================");
    }
}
