package controller;

import model.competition.TeamReportEntry;
import model.competition.TeamReportService;
import model.people.HeadCoach;
import model.people.Player;
import model.people.Position;
import model.team.Country;
import model.team.SquadValidation;
import model.team.Team;
import ui.fx.PlayerRow;
import ui.fx.TeamDetail;
import ui.fx.TeamRow;
import ui.fx.TeamsViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Arma los dos listados de la pagina Teams.
 *
 * Le pide al dominio el resumen alfabetico y la validacion de cada plantel, y
 * traduce todo a filas y fichas que la interfaz puede mostrar sin conocer
 * {@code Team}, {@code Player} ni {@code HeadCoach}.
 */
public class TeamsController {

    private static final String NOT_AVAILABLE = "-";
    private static final String EMPTY_MESSAGE = "No teams imported yet";
    private static final String VALID_SQUAD = "Squad complete: 18 players (2-6-5-5)";

    private final TournamentSession session;
    private final TeamsViewModel viewModel;
    private final TeamReportService teamReportService;

    public TeamsController(TournamentSession session, TeamsViewModel viewModel) {
        this(session, viewModel, new TeamReportService());
    }

    public TeamsController(
            TournamentSession session,
            TeamsViewModel viewModel,
            TeamReportService teamReportService) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
        this.teamReportService = Objects.requireNonNull(teamReportService, "The service is required");
    }

    /** Vuelve a leer el campeonato y rehace los dos listados. */
    public void refresh() {
        LocalDate today = LocalDate.now();
        List<Team> teams = session.getTeams();

        List<TeamReportEntry> report =
                teamReportService.computeReport(teams, session.getMatches(), today);

        viewModel.setRows(toRows(report));
        viewModel.setTeams(toDetails(report, today));
        viewModel.setStatus(teams.isEmpty() ? EMPTY_MESSAGE : "");
    }

    private List<TeamRow> toRows(List<TeamReportEntry> report) {
        List<TeamRow> rows = new ArrayList<>();
        for (TeamReportEntry entry : report) {
            rows.add(new TeamRow(
                    entry.getTeam().getName(),
                    formatDecimal(entry.getAveragePlayerAge()),
                    formatAge(entry.getCoachAge()),
                    countryNameOf(entry.getCoachCountry()),
                    entry.getGoalsFor(),
                    entry.getGoalsAgainst(),
                    formatDecimal(entry.getEffectiveness()) + "%"));
        }
        return rows;
    }

    /** Las fichas siguen el mismo orden alfabetico que el resumen. */
    private List<TeamDetail> toDetails(List<TeamReportEntry> report, LocalDate referenceDate) {
        List<TeamDetail> details = new ArrayList<>();
        for (TeamReportEntry entry : report) {
            details.add(toDetail(entry.getTeam(), referenceDate));
        }
        return details;
    }

    private TeamDetail toDetail(Team team, LocalDate referenceDate) {
        HeadCoach coach = team.getHeadCoach();
        SquadValidation validation = SquadValidation.of(team);

        return new TeamDetail(
                team.getName(),
                team.getCountry().getCountryName(),
                team.getRanking(),
                coach == null ? NOT_AVAILABLE : coach.getName(),
                coach == null ? NOT_AVAILABLE : formatAge(coach.getAge(referenceDate)),
                coach == null ? NOT_AVAILABLE : countryNameOf(coach.getCountry()),
                validation.isValid(),
                describeSquad(validation),
                toSquadRows(team, referenceDate));
    }

    private String describeSquad(SquadValidation validation) {
        String description;
        if (validation.isValid()) {
            description = VALID_SQUAD;
        } else {
            description = "Invalid squad: " + String.join("; ", validation.getProblems());
        }
        return description;
    }

    private List<PlayerRow> toSquadRows(Team team, LocalDate referenceDate) {
        List<PlayerRow> squad = new ArrayList<>();
        for (Player player : team.getPlayers()) {
            squad.add(new PlayerRow(
                    player.getName(),
                    labelOf(player.getPosition()),
                    player.getAge(referenceDate),
                    player.getAverage()));
        }
        return squad;
    }

    /** El enum del dominio no lleva textos de interfaz: se traducen aca. */
    private String labelOf(Position position) {
        return switch (position) {
            case GOALKEEPER -> "Goalkeeper";
            case DEFENDER -> "Defender";
            case MIDFIELDER -> "Midfielder";
            case FORWARD -> "Forward";
        };
    }

    private String countryNameOf(Country country) {
        return country == null ? NOT_AVAILABLE : country.getCountryName();
    }

    private String formatAge(int age) {
        return age <= 0 ? NOT_AVAILABLE : String.valueOf(age);
    }

    private String formatDecimal(double value) {
        return String.format(Locale.US, "%.1f", value);
    }
}
