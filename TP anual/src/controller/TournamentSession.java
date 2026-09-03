package controller;

import dataload.JsonTournamentLoader;
import dataload.SampleStadiumFactory;
import dataload.TournamentData;
import model.competition.Championship;
import model.competition.DrawService;
import model.competition.FixtureService;
import model.competition.Zone;
import model.match.Match;
import model.people.Referee;
import model.simulation.GroupStageSimulator;
import model.simulation.KnockoutStageResult;
import model.simulation.KnockoutStageSimulator;
import model.simulation.KnockoutTieReport;
import model.team.Team;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * El campeonato de la aplicacion, y su avance paso a paso.
 *
 * Arranca vacia: no hay nada cargado hasta que el usuario importa un archivo.
 * A partir de ahi el torneo avanza por acciones explicitas -- sorteo, fase de
 * grupos, fase eliminatoria -- en vez de simularse entero al abrir la ventana.
 *
 * El estado no se guarda en un campo: {@link #getState()} lo deduce de lo que
 * hay cargado y jugado, asi no puede quedar desincronizado.
 *
 * Los metodos son sincronizados porque las simulaciones corren en un hilo
 * aparte mientras la interfaz lee el estado desde el hilo de JavaFX.
 */
public class TournamentSession {

    private static final LocalDate GROUP_STAGE_START_DATE = LocalDate.of(2026, 9, 1);
    private static final LocalDate KNOCKOUT_STAGE_START_DATE = LocalDate.of(2026, 11, 1);
    private static final int DAYS_BETWEEN_ROUNDS = 7;

    private final Random seedGenerator = new Random();

    private TournamentData tournamentData;
    private Championship championship;
    private KnockoutStageResult knockoutResult;

    /** Importa el archivo de datos. Descarta cualquier torneo anterior. */
    public synchronized void importData(String dataPath) throws IOException {
        Objects.requireNonNull(dataPath, "The data path is required");
        TournamentData imported = new JsonTournamentLoader(dataPath).load();

        this.tournamentData = imported;
        this.championship = null;
        this.knockoutResult = null;
    }

    /** Sortea los grupos y genera el fixture de cada zona. */
    public synchronized void drawGroups() {
        requireState(TournamentState.DATA_LOADED, "the group draw");

        List<Zone> zones = new DrawService(new Random(seedGenerator.nextLong()))
                .draw(tournamentData.getTeams());
        FixtureService fixtureService = new FixtureService();
        for (Zone zone : zones) {
            fixtureService.generateFixture(zone, GROUP_STAGE_START_DATE, DAYS_BETWEEN_ROUNDS);
        }

        this.championship = new Championship(
                tournamentData.getTeams(),
                zones,
                tournamentData.getReferees(),
                new SampleStadiumFactory().createStadiums());
        this.knockoutResult = null;
    }

    /** Juega la fase de grupos completa. */
    public synchronized void playGroupStage() throws InterruptedException {
        requireState(TournamentState.GROUPS_DRAWN, "the group stage");

        new GroupStageSimulator().simulate(
                championship.getZones(),
                championship.getReferees(),
                seedGenerator.nextLong());
    }

    /** Juega la fase eliminatoria completa, hasta la final. */
    public synchronized void playKnockoutStage() throws InterruptedException {
        requireState(TournamentState.GROUP_STAGE_PLAYED, "the knockout stage");

        this.knockoutResult = new KnockoutStageSimulator().simulate(
                championship.getZones(),
                championship.getStadiums(),
                championship.getReferees(),
                KNOCKOUT_STAGE_START_DATE,
                seedGenerator.nextLong());
    }

    private void requireState(TournamentState expected, String action) {
        TournamentState current = getState();
        if (current != expected) {
            throw new IllegalStateException(
                    "Cannot run " + action + " while the tournament is: " + current.getLabel());
        }
    }

    /** Deduce el estado de lo que hay cargado y jugado. */
    public synchronized TournamentState getState() {
        TournamentState state;
        if (tournamentData == null) {
            state = TournamentState.EMPTY;
        } else if (championship == null) {
            state = TournamentState.DATA_LOADED;
        } else if (!allPlayed(getGroupMatches())) {
            state = TournamentState.GROUPS_DRAWN;
        } else if (knockoutResult == null) {
            state = TournamentState.GROUP_STAGE_PLAYED;
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

    /** Avisos que dejo la carga del archivo (datos incompletos, etc.). */
    public synchronized List<String> getWarnings() {
        return tournamentData == null ? List.of() : tournamentData.getWarnings();
    }

    public synchronized List<Zone> getZones() {
        return championship == null ? List.of() : championship.getZones();
    }

    public synchronized Championship getChampionship() {
        return championship;
    }

    /** Resultado de la fase eliminatoria, o {@code null} si todavia no se jugo. */
    public synchronized KnockoutStageResult getKnockoutResult() {
        return knockoutResult;
    }

    /** Solo los partidos de la fase de grupos. */
    public synchronized List<Match> getGroupMatches() {
        List<Match> groupMatches = new ArrayList<>();
        for (Zone zone : getZones()) {
            groupMatches.addAll(zone.getGroupMatches());
        }
        return groupMatches;
    }

    /** Todos los partidos del campeonato, de grupos y de eliminatorias. */
    public synchronized List<Match> getMatches() {
        List<Match> matches = new ArrayList<>(getGroupMatches());
        if (knockoutResult != null) {
            addTieMatches(matches, knockoutResult.getQuarterFinals());
            addTieMatches(matches, knockoutResult.getSemiFinals());
            matches.add(knockoutResult.getFinalMatchReport().getFinalMatch());
        }
        return matches;
    }

    private void addTieMatches(List<Match> matches, List<KnockoutTieReport> ties) {
        for (KnockoutTieReport tie : ties) {
            matches.add(tie.getFirstLeg());
            matches.add(tie.getSecondLeg());
        }
    }
}
