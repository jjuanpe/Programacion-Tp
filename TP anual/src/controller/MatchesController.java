package controller;

import model.event.Expulsion;
import model.event.Incidence;
import model.match.Formation;
import model.match.Match;
import model.match.PlayerParticipation;
import model.people.Player;
import model.people.Referee;
import model.venue.Stadium;
import model.competition.StandingEntry;
import model.simulation.MatchSimulationReport;
import viewmodel.StandingRow;
import viewmodel.IncidenceRow;
import viewmodel.LineupRow;
import viewmodel.MatchDetail;
import viewmodel.MatchesViewModel;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class MatchesController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
    private static final String NOT_AVAILABLE = "-";
    private static final String NO_MATCHES_MESSAGE =
            "No matches yet. Import the data and run the group draw from the Tournament page.";

    private final TournamentSession session;
    private final MatchesViewModel viewModel;

    public MatchesController(TournamentSession session, MatchesViewModel viewModel) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
        this.viewModel = Objects.requireNonNull(viewModel, "The view model is required");
    }

    public void refresh() {
        List<Match> matches = new ArrayList<>(session.getMatches());
        matches.sort(Comparator.comparing(Match::getDate));

        Map<Match, MatchSimulationReport> reports = new IdentityHashMap<>();
        for (MatchSimulationReport report : session.getGroupStageReports()) {
            reports.put(report.getMatch(), report);
        }

        List<MatchDetail> details = new ArrayList<>();
        for (Match match : matches) {
            details.add(toDetail(match, reports.get(match)));
        }
        viewModel.setMatches(details);
        if (details.isEmpty()) {
            viewModel.setStatus(NO_MATCHES_MESSAGE);
        } else {
            viewModel.setStatus("");
        }
    }

    private MatchDetail toDetail(Match match, MatchSimulationReport report) {
        boolean played = match.isPlayed();
        Map<Player, Integer> minutes = indexMinutes(match.getParticipations());

        String score;
        String status;
        if (played) {
            score = new StringBuilder().append(match.getHomeGoals()).append(" - ")
                    .append(match.getAwayGoals()).toString();
            status = "Played";
        } else {
            score = NOT_AVAILABLE;
            status = "Pending";
        }
        return new MatchDetail(
                match.getPhaseLabel(),
                match.getPhaseCategory(),
                match.getDate().format(DATE_FORMAT),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                score,
                status,
                played,
                refereeNameOf(match.getReferee()),
                stadiumNameOf(match.getStadium()),
                describeAggregate(match),
                toLineup(match.getHomeFormation(), minutes),
                toLineup(match.getAwayFormation(), minutes),
                toIncidences(match.getIncidences()),
                toStandingRows(report == null ? List.of() : report.getStandingsBefore()),
                toStandingRows(report == null ? List.of() : report.getStandingsAfter()));
    }

    private List<StandingRow> toStandingRows(List<StandingEntry> entries) {
        List<StandingRow> rows = new ArrayList<>();
        int position = 1;
        for (StandingEntry entry : entries) {
            rows.add(new StandingRow(
                    position++,
                    entry.getTeam().getName(),
                    entry.getPlayed(),
                    entry.getWon(),
                    entry.getDrawn(),
                    entry.getLost(),
                    entry.getGoalsFor(),
                    entry.getGoalsAgainst(),
                    entry.getGoalDifference(),
                    entry.getPoints(),
                    false));
        }
        return rows;
    }

    private String describeAggregate(Match match) {
        StringBuilder text = new StringBuilder(match.describeAggregate());
        if (match.hasPenaltyShootout()) {
            int home = match.getShootoutGoals(match.getHomeTeam());
            int away = match.getShootoutGoals(match.getAwayTeam());
            String winner = home > away ? match.getHomeTeam().getName() : match.getAwayTeam().getName();
            text.append(text.length() == 0 ? "Draw" : " (draw)");
            text.append(" | Penalties: ").append(home).append(" - ").append(away)
                    .append(" | Winner: ").append(winner);
        }
        Expulsion fifthRed = match.findFifthExpulsion(match.getIncidences());
        if (fifthRed != null) {
            if (text.length() > 0) {
                text.append(" | ");
            }
            text.append("Forfeit at ").append(fifthRed.getMinute()).append("': ")
                    .append(teamOf(fifthRed.getPlayer(), match))
                    .append(" received five red cards (0-3 loss)");
        }
        return text.toString();
    }

    private Map<Player, Integer> indexMinutes(List<PlayerParticipation> participations) {
        Map<Player, Integer> minutes = new HashMap<>();
        for (PlayerParticipation participation : participations) {
            minutes.put(participation.getPlayer(), participation.getMinutesPlayed());
        }
        return minutes;
    }

    private List<LineupRow> toLineup(Formation formation, Map<Player, Integer> minutes) {
        List<LineupRow> lineup = new ArrayList<>();
        if (formation != null) {
            for (Player player : formation.getStarters()) {
                lineup.add(toLineupRow(player, "Starter", minutes));
            }
            for (Player player : formation.getSubstitutes()) {
                lineup.add(toLineupRow(player, "Substitute", minutes));
            }
        }
        return lineup;
    }

    private LineupRow toLineupRow(Player player, String role, Map<Player, Integer> minutes) {
        Integer played = minutes.get(player);
        String minutesLabel;
        if (played == null || played == 0) {
            minutesLabel = NOT_AVAILABLE;
        } else {
            minutesLabel = String.valueOf(played);
        }
        return new LineupRow(
                player.getName(),
                player.getPosition().getLabel(),
                role,
                minutesLabel);
    }

    private List<IncidenceRow> toIncidences(List<Incidence> incidences) {
        List<Incidence> ordered = new ArrayList<>(incidences);
        ordered.sort(Comparator.comparingInt(Incidence::getMinute));

        List<IncidenceRow> rows = new ArrayList<>();
        for (Incidence incidence : ordered) {
            rows.add(new IncidenceRow(
                    incidence.getMinuteLabel(),
                    incidence.getTypeLabel(),
                    incidence.getDetail()));
        }
        return rows;
    }

    private String teamOf(Player player, Match match) {
        String teamName;
        if (match.getHomeTeam().getPlayers().contains(player)) {
            teamName = match.getHomeTeam().getName();
        } else if (match.getAwayTeam().getPlayers().contains(player)) {
            teamName = match.getAwayTeam().getName();
        } else {
            teamName = "";
        }
        return teamName;
    }

    private String refereeNameOf(Referee referee) {
        String name;
        if (referee == null) {
            name = NOT_AVAILABLE;
        } else {
            name = referee.getName();
        }
        return name;
    }

    private String stadiumNameOf(Stadium stadium) {
        String name;
        if (stadium == null) {
            name = NOT_AVAILABLE;
        } else {
            name = new StringBuilder(stadium.getName()).append(" (")
                    .append(stadium.getCity().getName()).append(')').toString();
        }
        return name;
    }
}
