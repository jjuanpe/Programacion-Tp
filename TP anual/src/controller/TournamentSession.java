package controller;

import dataload.JsonTournamentLoader;
import dao.StadiumDAO;
import dataload.TournamentData;
import model.competition.Championship;
import model.competition.DrawService;
import model.competition.FixtureService;
import model.competition.Zone;
import model.match.Match;
import model.people.Referee;
import model.simulation.FinalMatchReport;
import model.simulation.GroupStageSimulator;
import model.simulation.KnockoutStageContext;
import model.simulation.KnockoutStageResult;
import model.simulation.KnockoutStageSimulator;
import model.simulation.KnockoutTieReport;
import model.team.Team;
import model.venue.Stadium;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TournamentSession {

    private static final LocalDate GROUP_STAGE_START_DATE = LocalDate.of(2026, 9, 1);
    private static final LocalDate KNOCKOUT_STAGE_START_DATE = LocalDate.of(2026, 11, 1);
    private static final int DAYS_BETWEEN_ROUNDS = 7;
    private static final int DAYS_BETWEEN_KNOCKOUT_STAGES = 14;

    private final Random seedGenerator = new Random();

    private TournamentData tournamentData;
    private Championship championship;
    private KnockoutStageContext knockoutContext;
    private List<KnockoutTieReport> quarterFinalReports;
    private List<KnockoutTieReport> semiFinalReports;
    private KnockoutStageResult knockoutResult;

    public synchronized void importData(String dataPath) throws IOException {
        Objects.requireNonNull(dataPath, "The data path is required");
        TournamentData imported = new JsonTournamentLoader(dataPath).load();

        this.tournamentData = imported;
        this.championship = null;
        resetKnockoutProgress();
    }

    public synchronized void drawGroups() throws SQLException {
        requireState(TournamentState.DATA_LOADED, "the group draw");

        List<Stadium> stadiums = new StadiumDAO().getAllStadiums();
        if (stadiums.size() < 13) {
            throw new IllegalStateException("At least 13 stadiums are required for the knockout stage; found "
                    + stadiums.size());
        }
        assignHomeStadiums(tournamentData.getTeams(), stadiums, new Random(seedGenerator.nextLong()));

        List<Zone> zones = new DrawService(new Random(seedGenerator.nextLong()))
                .draw(tournamentData.getTeams());
        FixtureService fixtureService = new FixtureService();
        for (Zone zone : zones) {
            fixtureService.generateFixture(zone, GROUP_STAGE_START_DATE, DAYS_BETWEEN_ROUNDS,
                    stadiums, new Random(seedGenerator.nextLong()));
        }

        this.championship = new Championship(
                tournamentData.getTeams(),
                zones,
                tournamentData.getReferees(),
                stadiums);
        resetKnockoutProgress();
    }

    private void assignHomeStadiums(List<Team> teams, List<Stadium> stadiums, Random random) {
        List<Stadium> shuffledStadiums = new ArrayList<>(stadiums);
        Collections.shuffle(shuffledStadiums, random);
        for (int index = 0; index < teams.size(); index++) {
            teams.get(index).setStadium(shuffledStadiums.get(index % shuffledStadiums.size()));
        }
    }

    private void resetKnockoutProgress() {
        this.knockoutContext = null;
        this.quarterFinalReports = null;
        this.semiFinalReports = null;
        this.knockoutResult = null;
    }

    public synchronized void playGroupStage() throws InterruptedException {
        requireState(TournamentState.GROUPS_DRAWN, "the group stage");

        new GroupStageSimulator().simulate(
                championship.getZones(),
                championship.getReferees(),
                seedGenerator.nextLong());
    }

    public synchronized void playQuarterFinals() throws InterruptedException {
        requireState(TournamentState.GROUP_STAGE_PLAYED, "the quarterfinals");

        KnockoutStageSimulator simulator = new KnockoutStageSimulator();
        knockoutContext = simulator.startKnockoutStage(
                championship.getStadiums(), championship.getReferees(), seedGenerator.nextLong());
        quarterFinalReports = simulator.playQuarterFinals(
                championship.getZones(), knockoutContext, KNOCKOUT_STAGE_START_DATE);
        consumeStadiumsOfTies(quarterFinalReports);
    }

    public synchronized void playSemiFinals() throws InterruptedException {
        requireState(TournamentState.QUARTER_FINALS_PLAYED, "the semifinals");

        LocalDate semiFinalDate = KNOCKOUT_STAGE_START_DATE.plusDays(DAYS_BETWEEN_KNOCKOUT_STAGES);
        semiFinalReports = new KnockoutStageSimulator().playSemiFinals(
                quarterFinalReports, knockoutContext, semiFinalDate);
        consumeStadiumsOfTies(semiFinalReports);
    }

    public synchronized void playFinal() {
        requireState(TournamentState.SEMI_FINALS_PLAYED, "the final");

        LocalDate finalDate = KNOCKOUT_STAGE_START_DATE.plusDays(2 * DAYS_BETWEEN_KNOCKOUT_STAGES);
        FinalMatchReport finalReport = new KnockoutStageSimulator().playFinalMatch(
                semiFinalReports, knockoutContext, finalDate);
        championship.consumeStadium(finalReport.getFinalMatch().getStadium());

        this.knockoutResult = new KnockoutStageResult(quarterFinalReports, semiFinalReports, finalReport);
    }

    private void consumeStadiumsOfTies(List<KnockoutTieReport> ties) {
        List<Match> matches = new ArrayList<>();
        addTieMatches(matches, ties);
        for (Match match : matches) {
            championship.consumeStadium(match.getStadium());
        }
    }

    private void requireState(TournamentState expected, String action) {
        TournamentState current = getState();
        if (current != expected) {
            throw new IllegalStateException(
                    "Cannot run " + action + " while the tournament is: " + current.getLabel());
        }
    }

    public synchronized TournamentState getState() {
        TournamentState state;
        if (tournamentData == null) {
            state = TournamentState.EMPTY;
        } else if (championship == null) {
            state = TournamentState.DATA_LOADED;
        } else if (!allPlayed(getGroupMatches())) {
            state = TournamentState.GROUPS_DRAWN;
        } else if (quarterFinalReports == null) {
            state = TournamentState.GROUP_STAGE_PLAYED;
        } else if (semiFinalReports == null) {
            state = TournamentState.QUARTER_FINALS_PLAYED;
        } else if (knockoutResult == null) {
            state = TournamentState.SEMI_FINALS_PLAYED;
        } else {
            state = TournamentState.FINISHED;
        }
        return state;
    }

    private boolean allPlayed(List<Match> matches) {
        boolean allPlayed = !matches.isEmpty();
        for (Match match : matches) {
            if (!match.isPlayed()) {
                allPlayed = false;
            }
        }
        return allPlayed;
    }

    public synchronized List<Team> getTeams() {
        return tournamentData == null ? List.of() : tournamentData.getTeams();
    }

    public synchronized List<Referee> getReferees() {
        return tournamentData == null ? List.of() : tournamentData.getReferees();
    }

    public synchronized List<String> getWarnings() {
        return tournamentData == null ? List.of() : tournamentData.getWarnings();
    }

    public synchronized List<Zone> getZones() {
        return championship == null ? List.of() : championship.getZones();
    }

    public synchronized Championship getChampionship() {
        return championship;
    }

    public synchronized KnockoutStageResult getKnockoutResult() {
        return knockoutResult;
    }

    public synchronized List<Match> getGroupMatches() {
        List<Match> groupMatches = new ArrayList<>();
        for (Zone zone : getZones()) {
            groupMatches.addAll(zone.getGroupMatches());
        }
        return groupMatches;
    }

    public synchronized List<Match> getMatches() {
        List<Match> matches = new ArrayList<>(getGroupMatches());
        if (quarterFinalReports != null) {
            addTieMatches(matches, quarterFinalReports);
        }
        if (semiFinalReports != null) {
            addTieMatches(matches, semiFinalReports);
        }
        if (knockoutResult != null) {
            matches.add(knockoutResult.getFinalMatchReport().getFinalMatch());
        }
        return matches;
    }

    public synchronized List<KnockoutTieReport> getQuarterFinalReports() {
        return quarterFinalReports == null ? List.of() : List.copyOf(quarterFinalReports);
    }

    public synchronized List<KnockoutTieReport> getSemiFinalReports() {
        return semiFinalReports == null ? List.of() : List.copyOf(semiFinalReports);
    }

    private void addTieMatches(List<Match> matches, List<KnockoutTieReport> ties) {
        for (KnockoutTieReport tie : ties) {
            matches.add(tie.getFirstLeg());
            matches.add(tie.getSecondLeg());
        }
    }
}
