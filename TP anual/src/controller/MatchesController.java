package controller;

import model.event.Change;
import model.event.PlayEvent;
import model.event.Expulsion;
import model.event.Goal;
import model.event.Incidence;
import model.event.PenaltyExecuted;
import model.event.YellowCard;
import model.match.FinalMatch;
import model.match.FirstLegMatch;
import model.match.Formation;
import model.match.Match;
import model.match.PlayerParticipation;
import model.match.SecondLegMatch;
import model.people.Player;
import model.people.Position;
import model.people.Referee;
import model.venue.Stadium;
import ui.fx.IncidenceRow;
import ui.fx.LineupRow;
import ui.fx.MatchDetail;
import ui.fx.MatchesViewModel;

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

        List<MatchDetail> details = new ArrayList<>();
        for (Match match : matches) {
            details.add(toDetail(match));
        }
        viewModel.setMatches(details);
        if (details.isEmpty()) {
            viewModel.setStatus(NO_MATCHES_MESSAGE);
        } else {
            viewModel.setStatus("");
        }
    }

    private MatchDetail toDetail(Match match) {
        boolean played = match.isPlayed();
        Map<Player, Integer> minutes = indexMinutes(match.getParticipations());

        String score;
        String status;
        if (played) {
            score = match.getHomeGoals() + " - " + match.getAwayGoals();
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
                toIncidences(match.getIncidences()));
    }

    private String describeAggregate(Match match) {
        String aggregate = "";
        if (match instanceof SecondLegMatch secondLeg
                && secondLeg.isPlayed()
                && secondLeg.getFirstLeg().isPlayed()) {
            aggregate = "Aggregate: "
                    + secondLeg.getPlainAggregateForSecondLegHomeTeam() + " - "
                    + secondLeg.getPlainAggregateForSecondLegAwayTeam();
        }
        int home = 0;
        int away = 0;
        boolean hasShootout = false;
        for (Incidence incidence : match.getIncidences()) {
            if (incidence instanceof PenaltyExecuted penalty) {
                hasShootout = true;
                if (penalty.isScored()) {
                    if (match.getHomeTeam().getPlayers().contains(penalty.getKicker())) {
                        home++;
                    } else {
                        away++;
                    }
                }
            }
        }
        if (hasShootout) {
            String winner;
            if (home > away) {
                winner = match.getHomeTeam().getName();
            } else {
                winner = match.getAwayTeam().getName();
            }
            if (aggregate.isEmpty()) {
                aggregate = "Draw";
            } else {
                aggregate += " (draw)";
            }
            aggregate += " | Penalties: " + home + " - " + away + " | Winner: " + winner;
        }
        Expulsion fifthRed = match.findFifthExpulsion(match.getIncidences());
        if (fifthRed != null) {
            if (!aggregate.isEmpty()) {
                aggregate += " | ";
            }
            aggregate += "Forfeit at "
                    + fifthRed.getMinute() + "': " + teamOf(fifthRed.getPlayer(), match)
                    + " received five red cards (0-3 loss)";
        }
        return aggregate;
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
            String minuteLabel;
            if (incidence instanceof PenaltyExecuted) {
                minuteLabel = "Kick " + (incidence.getMinute() - 120);
            } else {
                minuteLabel = incidence.getMinute() + "'";
            }
            rows.add(new IncidenceRow(
                    minuteLabel,
                    typeOf(incidence),
                    detailOf(incidence)));
        }
        return rows;
    }

    private String typeOf(Incidence incidence) {
        String type;
        if (incidence instanceof PlayEvent play) {
            type = play.getType();
        } else if (incidence instanceof Goal goal) {
            if (goal.isOwnGoal()) {
                type = "Own goal";
            } else if (goal.isPenalty()) {
                type = "Goal (penalty)";
            } else {
                type = "Goal";
            }
        } else if (incidence instanceof YellowCard) {
            type = "Yellow card";
        } else if (incidence instanceof Expulsion) {
            type = "Expulsion";
        } else if (incidence instanceof Change) {
            type = "Substitution";
        } else if (incidence instanceof PenaltyExecuted) {
            type = "Shoot-out penalty";
        } else {
            type = incidence.getClass().getSimpleName();
        }
        return type;
    }

    private String detailOf(Incidence incidence) {
        Match match = incidence.getMatch();
        String detail;
        if (incidence instanceof PlayEvent play) {
            detail = withTeam(play.getPlayer(), match);
        } else if (incidence instanceof Goal goal) {
            detail = withTeam(goal.getScorer(), match);
            if (goal.getGoalkeeper() != null) {
                detail = detail + " (goalkeeper: " + goal.getGoalkeeper().getName() + ")";
            }
        } else if (incidence instanceof YellowCard card) {
            detail = withTeam(card.getPlayer(), match);
        } else if (incidence instanceof Expulsion expulsion) {
            detail = withTeam(expulsion.getPlayer(), match);
            if (expulsion.getReason() != null) {
                detail = detail + " (" + expulsion.getReason().getDescription() + ")";
            }
            if (match.findFifthExpulsion(match.getIncidences()) == expulsion) {
                detail += " - Fifth red card: match abandoned, team loses 0-3";
            }
        } else if (incidence instanceof Change change) {
            detail = withTeam(change.getPlayerOut(), match)
                    + " -> " + change.getPlayerIn().getName();
        } else if (incidence instanceof PenaltyExecuted penalty) {
            detail = withTeam(penalty.getKicker(), match) + (" " + penalty.getOutcomeDescription());
        } else {
            detail = incidence.getDescription();
        }
        return detail;
    }

    private String withTeam(Player player, Match match) {
        String teamName = teamOf(player, match);
        String description;
        if (teamName.isEmpty()) {
            description = player.getName();
        } else {
            description = teamName + " - " + player.getName();
        }
        return description;
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
            name = stadium.getName() + " (" + stadium.getCity().getName() + ")";
        }
        return name;
    }
}
