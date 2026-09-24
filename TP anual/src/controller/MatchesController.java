package controller;

import model.event.Change;
import model.event.Expulsion;
import model.event.Goal;
import model.event.Incidence;
import model.event.PenaltyExecuted;
import model.event.YellowCard;
import model.match.FinalMatch;
import model.match.FirstLegMatch;
import model.match.Formation;
import model.match.GroupMatch;
import model.match.Match;
import model.match.PhaseType;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MatchesController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
    private static final String NOT_AVAILABLE = "-";
    private static final String GROUP_STAGE_CATEGORY = "Group Stage";
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
        viewModel.setStatus(details.isEmpty() ? NO_MATCHES_MESSAGE : "");
    }

    private MatchDetail toDetail(Match match) {
        boolean played = match.isPlayed();
        Map<Player, Integer> minutes = indexMinutes(match.getParticipations());

        return new MatchDetail(
                describePhase(match),
                phaseCategory(match),
                match.getDate().format(DATE_FORMAT),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                played ? match.getHomeGoals() + " - " + match.getAwayGoals() : NOT_AVAILABLE,
                played ? "Played" : "Pending",
                played,
                refereeNameOf(match.getReferee()),
                stadiumNameOf(match.getStadium()),
                describeAggregate(match),
                toLineup(match.getHomeFormation(), minutes),
                toLineup(match.getAwayFormation(), minutes),
                toIncidences(match.getIncidences()));
    }

    private String describePhase(Match match) {
        String phase;
        if (match instanceof GroupMatch) {
            phase = GROUP_STAGE_CATEGORY;
        } else if (match instanceof SecondLegMatch secondLeg) {
            phase = labelOf(secondLeg.getPhase()) + " (2nd leg)";
        } else if (match instanceof FirstLegMatch firstLeg) {
            phase = labelOf(firstLeg.getPhase()) + " (1st leg)";
        } else if (match instanceof FinalMatch) {
            phase = "Final";
        } else {
            phase = NOT_AVAILABLE;
        }
        return phase;
    }

    private String phaseCategory(Match match) {
        String category;
        if (match instanceof GroupMatch) {
            category = GROUP_STAGE_CATEGORY;
        } else if (match instanceof SecondLegMatch secondLeg) {
            category = labelOf(secondLeg.getPhase());
        } else if (match instanceof FirstLegMatch firstLeg) {
            category = labelOf(firstLeg.getPhase());
        } else if (match instanceof FinalMatch) {
            category = "Final";
        } else {
            category = NOT_AVAILABLE;
        }
        return category;
    }

    private String labelOf(PhaseType phase) {
        return switch (phase) {
            case QUARTER_FINAL -> "Quarter-final";
            case SEMI_FINAL -> "Semi-final";
            case FINAL -> "Final";
        };
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
        return new LineupRow(
                player.getName(),
                labelOf(player.getPosition()),
                role,
                played == null || played == 0 ? NOT_AVAILABLE : String.valueOf(played));
    }

    private String labelOf(Position position) {
        return switch (position) {
            case GOALKEEPER -> "Goalkeeper";
            case DEFENDER -> "Defender";
            case MIDFIELDER -> "Midfielder";
            case FORWARD -> "Forward";
        };
    }

    private List<IncidenceRow> toIncidences(List<Incidence> incidences) {
        List<Incidence> ordered = new ArrayList<>(incidences);
        ordered.sort(Comparator.comparingInt(Incidence::getMinute));

        List<IncidenceRow> rows = new ArrayList<>();
        for (Incidence incidence : ordered) {
            rows.add(new IncidenceRow(
                    incidence.getMinute() + "'",
                    typeOf(incidence),
                    detailOf(incidence)));
        }
        return rows;
    }

    private String typeOf(Incidence incidence) {
        String type;
        if (incidence instanceof Goal goal) {
            type = goal.isOwnGoal() ? "Own goal" : goal.isPenalty() ? "Goal (penalty)" : "Goal";
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
        if (incidence instanceof Goal goal) {
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
        } else if (incidence instanceof Change change) {
            detail = withTeam(change.getPlayerOut(), match)
                    + " -> " + change.getPlayerIn().getName();
        } else if (incidence instanceof PenaltyExecuted penalty) {
            detail = withTeam(penalty.getKicker(), match) + (penalty.isScored() ? " scored" : " missed");
        } else {
            detail = incidence.getDescription();
        }
        return detail;
    }

    private String withTeam(Player player, Match match) {
        String teamName = teamOf(player, match);
        return teamName.isEmpty() ? player.getName() : teamName + " - " + player.getName();
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
        return referee == null ? NOT_AVAILABLE : referee.getName();
    }

    private String stadiumNameOf(Stadium stadium) {
        return stadium == null ? NOT_AVAILABLE : stadium.getName() + " (" + stadium.getCity().getName() + ")";
    }
}
