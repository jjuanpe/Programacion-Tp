package app;

import dataload.JsonTournamentLoader;
import dataload.TournamentData;
import model.competition.DrawService;
import model.competition.FixtureService;
import model.competition.Zone;
import model.simulation.GroupStageSimulator;
import model.simulation.KnockoutStageResult;
import model.simulation.KnockoutStageSimulator;
import model.simulation.KnockoutTieReport;
import model.simulation.MatchSimulationReport;
import model.team.Country;
import model.venue.City;
import model.venue.Stadium;
import ui.ConsoleReportFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sprint4Main {

    private static final String DEFAULT_DATA_PATH = "TP anual/docs/torneo.json";
    private static final LocalDate GROUP_STAGE_START_DATE = LocalDate.of(2026, 9, 1);
    private static final LocalDate KNOCKOUT_STAGE_START_DATE = LocalDate.of(2026, 11, 1);
    private static final int DAYS_BETWEEN_ROUNDS = 7;

    public static void main(String[] args) throws Exception {
        String dataPath = args.length == 0 ? DEFAULT_DATA_PATH : args[0];
        long drawSeed = args.length >= 2 ? Long.parseLong(args[1]) : new Random().nextLong();
        long groupStageSeed = args.length >= 3 ? Long.parseLong(args[2]) : new Random().nextLong();
        long knockoutSeed = args.length >= 4 ? Long.parseLong(args[3]) : new Random().nextLong();

        System.out.println("Draw seed: " + drawSeed);
        System.out.println("Group stage seed: " + groupStageSeed);
        System.out.println("Knockout stage seed: " + knockoutSeed);

        TournamentData tournamentData = new JsonTournamentLoader(dataPath).load();

        List<Zone> zones = new DrawService(new Random(drawSeed)).draw(tournamentData.getTeams());
        FixtureService fixtureService = new FixtureService();
        for (Zone zone : zones) {
            fixtureService.generateFixture(zone, GROUP_STAGE_START_DATE, DAYS_BETWEEN_ROUNDS);
        }

        GroupStageSimulator groupStageSimulator = new GroupStageSimulator();
        List<MatchSimulationReport> groupStageReports =
                groupStageSimulator.simulate(zones, tournamentData.getReferees(), groupStageSeed);

        ConsoleReportFormatter formatter = new ConsoleReportFormatter();
        System.out.println();
        System.out.println("========================================================");
        System.out.println(" GROUP STAGE");
        System.out.println("========================================================");
        for (MatchSimulationReport report : groupStageReports) {
            System.out.print(formatter.format(report));
        }

        List<Stadium> stadiums = createSampleStadiums();
        KnockoutStageSimulator knockoutStageSimulator = new KnockoutStageSimulator();
        KnockoutStageResult knockoutResult = knockoutStageSimulator.simulate(
                zones, stadiums, tournamentData.getReferees(), KNOCKOUT_STAGE_START_DATE, knockoutSeed);

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

    private static List<Stadium> createSampleStadiums() {
        List<Stadium> stadiums = new ArrayList<>();
        String[][] cityAndStadium = {
                {"Buenos Aires", "Argentina", "Estadio Monumental"},
                {"Buenos Aires", "Argentina", "La Bombonera"},
                {"Rio de Janeiro", "Brazil", "Maracana"},
                {"Sao Paulo", "Brazil", "Morumbi"},
                {"Montevideo", "Uruguay", "Centenario"},
                {"Santiago", "Chile", "Estadio Nacional"},
                {"Bogota", "Colombia", "El Campin"},
                {"Lima", "Peru", "Estadio Nacional de Lima"},
                {"Asuncion", "Paraguay", "Defensores del Chaco"},
                {"Guayaquil", "Ecuador", "Monumental Banco Pichincha"},
                {"Madrid", "Spain", "Santiago Bernabeu"},
                {"Milan", "Italy", "San Siro"},
                {"Munich", "Germany", "Allianz Arena"},
        };
        long id = 1;
        for (String[] row : cityAndStadium) {
            City city = new City(id, row[0], new Country(row[1]));
            stadiums.add(new Stadium(id, row[2], city, 50000));
            id = id + 1;
        }
        return stadiums;
    }
}
