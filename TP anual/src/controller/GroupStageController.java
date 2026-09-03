package controller;

import model.competition.KnockoutBracketService;
import model.competition.StandingEntry;
import model.competition.StandingsService;
import model.competition.Zone;
import model.match.GroupMatch;
import model.people.Referee;
import model.team.Team;
import ui.fx.FixtureRow;
import ui.fx.GroupStageViewModel;
import ui.fx.GroupZone;
import ui.fx.StandingRow;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Arma la pagina Group Stage: las cuatro zonas con sus equipos, su fixture y
 * su tabla de posiciones.
 *
 * Las posiciones las calcula {@link StandingsService}, que ya aplica todos los
 * criterios de desempate del TP. Los clasificados son los primeros
 * {@link KnockoutBracketService#QUALIFIED_PER_ZONE} de cada zona: se toma la
 * constante del servicio que arma el cuadro, para que no se puedan
 * desincronizar.
 */
public class GroupStageController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
    private static final String NOT_AVAILABLE = "-";
    private static final String NO_DATA_MESSAGE =
            "No tournament data imported yet. Import it from the Tournament page.";
    private static final String NO_DRAW_MESSAGE =
            "Data imported, but the group draw has not been run yet. Use Run group draw above.";

    private final TournamentSession session;
    private final GroupStageViewModel viewModel;
    private final StandingsService standingsService;

    public GroupStageController(TournamentSession session, GroupStageViewModel viewModel) {
        this(session, viewModel, new StandingsService());
    }

    public GroupStageController(
            TournamentSession session,
            GroupStageViewModel viewModel,
            StandingsService standingsService) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
        this.standingsService = Objects.requireNonNull(standingsService, "The service is required");
    }

    /** Vuelve a leer las zonas y rehace tablas y fixtures. */
    public void refresh() {
        List<Zone> zones = session.getZones();
        List<GroupZone> groupZones = new ArrayList<>();
        for (Zone zone : zones) {
            groupZones.add(toGroupZone(zone));
        }
        viewModel.setZones(groupZones);
        viewModel.setStatus(describeEmptyState());
    }

    /**
     * Que decir cuando no hay zonas para mostrar. El mensaje explica que falta
     * hacer: sin esto, la pagina se veia vacia y con el boton de sorteo
     * deshabilitado, sin ninguna pista de por que.
     */
    private String describeEmptyState() {
        String message;
        TournamentState state = session.getState();
        if (state == TournamentState.EMPTY) {
            message = NO_DATA_MESSAGE;
        } else if (state == TournamentState.DATA_LOADED) {
            message = NO_DRAW_MESSAGE;
        } else {
            message = "";
        }
        return message;
    }

    private GroupZone toGroupZone(Zone zone) {
        List<StandingEntry> standings = standingsService.computeStandings(zone);
        return new GroupZone(
                zone.getName(),
                describeTeams(zone),
                toStandingRows(standings),
                toFixtureRows(zone.getGroupMatches()),
                describeQualified(standings));
    }

    /** Los equipos que el sorteo asigno a la zona, en el orden del sorteo. */
    private String describeTeams(Zone zone) {
        List<String> names = new ArrayList<>();
        for (Team team : zone.getTeams()) {
            names.add(team.getName());
        }
        return String.join(", ", names);
    }

    private List<StandingRow> toStandingRows(List<StandingEntry> standings) {
        List<StandingRow> rows = new ArrayList<>();
        int position = 0;
        for (StandingEntry entry : standings) {
            position++;
            rows.add(new StandingRow(
                    position,
                    entry.getTeam().getName(),
                    entry.getPlayed(),
                    entry.getWon(),
                    entry.getDrawn(),
                    entry.getLost(),
                    entry.getGoalsFor(),
                    entry.getGoalsAgainst(),
                    entry.getGoalDifference(),
                    entry.getPoints(),
                    position <= KnockoutBracketService.QUALIFIED_PER_ZONE));
        }
        return rows;
    }

    private List<FixtureRow> toFixtureRows(List<GroupMatch> matches) {
        List<FixtureRow> rows = new ArrayList<>();
        for (GroupMatch match : matches) {
            boolean played = match.isPlayed();
            rows.add(new FixtureRow(
                    match.getDate().format(DATE_FORMAT),
                    match.getHomeTeam().getName(),
                    played ? match.getHomeGoals() + " - " + match.getAwayGoals() : NOT_AVAILABLE,
                    match.getAwayTeam().getName(),
                    played ? "Played" : "Pending",
                    refereeNameOf(match.getReferee())));
        }
        return rows;
    }

    private String refereeNameOf(Referee referee) {
        return referee == null ? NOT_AVAILABLE : referee.getName();
    }

    /**
     * Los clasificados solo tienen sentido con la zona terminada: mientras
     * queden partidos por jugar, las posiciones todavia pueden cambiar.
     */
    private String describeQualified(List<StandingEntry> standings) {
        String description;
        if (!isZoneFinished(standings)) {
            description = "Qualified: still to be decided";
        } else {
            List<String> qualified = new ArrayList<>();
            for (int index = 0;
                    index < KnockoutBracketService.QUALIFIED_PER_ZONE && index < standings.size();
                    index++) {
                qualified.add(standings.get(index).getTeam().getName());
            }
            description = "Qualified: " + String.join(", ", qualified);
        }
        return description;
    }

    private boolean isZoneFinished(List<StandingEntry> standings) {
        int expectedMatchesPerTeam = standings.size() - 1;
        boolean finished = !standings.isEmpty();
        for (StandingEntry entry : standings) {
            if (entry.getPlayed() != expectedMatchesPerTeam) {
                finished = false;
            }
        }
        return finished;
    }
}
