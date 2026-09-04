package controller;

import model.competition.PlayerReportEntry;
import model.competition.PlayerReportService;
import model.people.Player;
import model.people.Position;
import model.team.Team;
import ui.fx.PlayerStatsRow;
import ui.fx.PlayersViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Arma la pagina Players: el listado de jugadores del campeonato, ordenado
 * por equipo, con sus estadisticas (partidos jugados, minutos, goles y,
 * para los arqueros, goles recibidos y su promedio por partido).
 *
 * El filtro por posicion lo aplica la vista sobre el listado completo que
 * arma este controlador.
 */
public class PlayersController {

    private static final String NOT_AVAILABLE = "-";
    private static final String EMPTY_MESSAGE = "No players imported yet";

    private final TournamentSession session;
    private final PlayersViewModel viewModel;
    private final PlayerReportService playerReportService;

    public PlayersController(TournamentSession session, PlayersViewModel viewModel) {
        this(session, viewModel, new PlayerReportService());
    }

    public PlayersController(
            TournamentSession session,
            PlayersViewModel viewModel,
            PlayerReportService playerReportService) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
        this.playerReportService = Objects.requireNonNull(playerReportService, "The service is required");
    }

    /** Vuelve a leer el campeonato y rehace el listado de jugadores. */
    public void refresh() {
        LocalDate today = LocalDate.now();
        List<Team> teams = session.getTeams();
        List<PlayerReportEntry> report = playerReportService.computeReport(teams, session.getMatches());

        List<PlayerStatsRow> rows = new ArrayList<>();
        for (PlayerReportEntry entry : report) {
            rows.add(toRow(entry, today));
        }
        viewModel.setPlayers(rows);
        viewModel.setStatus(rows.isEmpty() ? EMPTY_MESSAGE : "");
    }

    private PlayerStatsRow toRow(PlayerReportEntry entry, LocalDate referenceDate) {
        Player player = entry.getPlayer();
        boolean isGoalkeeper = player.getPosition() == Position.GOALKEEPER;
        return new PlayerStatsRow(
                player.getName(),
                labelOf(player.getPosition()),
                entry.getTeam().getName(),
                player.getAge(referenceDate),
                player.getAverage(),
                entry.getMatchesPlayed(),
                entry.getMinutesPlayed(),
                entry.getGoals(),
                isGoalkeeper ? String.valueOf(entry.getGoalsConceded()) : NOT_AVAILABLE,
                isGoalkeeper ? formatDecimal(entry.getGoalsConcededAverage()) : NOT_AVAILABLE);
    }

    private String labelOf(Position position) {
        return switch (position) {
            case GOALKEEPER -> "Goalkeeper";
            case DEFENDER -> "Defender";
            case MIDFIELDER -> "Midfielder";
            case FORWARD -> "Forward";
        };
    }

    private String formatDecimal(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
