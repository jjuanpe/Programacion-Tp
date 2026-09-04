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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Traduce un {@link Match} del dominio a un {@link MatchDetail} de interfaz.
 *
 * Lo usan tanto la pagina Matches (todas las fases) como la pagina Knockout
 * Stage (solo las eliminatorias), asi la conversion de alineaciones e
 * incidencias no se duplica entre las dos.
 */
final class MatchDetailFactory {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
    private static final String NOT_AVAILABLE = "-";
    private static final String GROUP_STAGE_CATEGORY = "Group Stage";

    private MatchDetailFactory() {
    }

    static MatchDetail toDetail(Match match) {
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

    /** A que instancia del torneo pertenece el partido. */
    private static String describePhase(Match match) {
        String phase;
        if (match instanceof GroupMatch) {
            phase = GROUP_STAGE_CATEGORY;
        } else if (match instanceof FirstLegMatch firstLeg) {
            phase = labelOf(firstLeg.getPhase()) + " (1st leg)";
        } else if (match instanceof SecondLegMatch secondLeg) {
            phase = labelOf(secondLeg.getPhase()) + " (2nd leg)";
        } else if (match instanceof FinalMatch) {
            phase = "Final";
        } else {
            phase = NOT_AVAILABLE;
        }
        return phase;
    }

    /** Fase sin distinguir ida/vuelta, para agrupar y filtrar. */
    private static String phaseCategory(Match match) {
        String category;
        if (match instanceof GroupMatch) {
            category = GROUP_STAGE_CATEGORY;
        } else if (match instanceof FirstLegMatch firstLeg) {
            category = labelOf(firstLeg.getPhase());
        } else if (match instanceof SecondLegMatch secondLeg) {
            category = labelOf(secondLeg.getPhase());
        } else if (match instanceof FinalMatch) {
            category = "Final";
        } else {
            category = NOT_AVAILABLE;
        }
        return category;
    }

    private static String labelOf(PhaseType phase) {
        return switch (phase) {
            case QUARTER_FINAL -> "Quarter-final";
            case SEMI_FINAL -> "Semi-final";
            case FINAL -> "Final";
        };
    }

    /** El global solo tiene sentido en la vuelta, con la ida ya jugada. */
    private static String describeAggregate(Match match) {
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

    private static Map<Player, Integer> indexMinutes(List<PlayerParticipation> participations) {
        Map<Player, Integer> minutes = new HashMap<>();
        for (PlayerParticipation participation : participations) {
            minutes.put(participation.getPlayer(), participation.getMinutesPlayed());
        }
        return minutes;
    }

    /** La alineacion existe solo si el partido se jugo. */
    private static List<LineupRow> toLineup(Formation formation, Map<Player, Integer> minutes) {
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

    private static LineupRow toLineupRow(Player player, String role, Map<Player, Integer> minutes) {
        Integer played = minutes.get(player);
        return new LineupRow(
                player.getName(),
                labelOf(player.getPosition()),
                role,
                played == null || played == 0 ? NOT_AVAILABLE : String.valueOf(played));
    }

    private static String labelOf(Position position) {
        return switch (position) {
            case GOALKEEPER -> "Goalkeeper";
            case DEFENDER -> "Defender";
            case MIDFIELDER -> "Midfielder";
            case FORWARD -> "Forward";
        };
    }

    /** Las incidencias van en orden de minuto, como se vieron en la cancha. */
    private static List<IncidenceRow> toIncidences(List<Incidence> incidences) {
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

    private static String typeOf(Incidence incidence) {
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

    private static String detailOf(Incidence incidence) {
        String detail;
        if (incidence instanceof Goal goal) {
            detail = goal.getScorer().getName();
            if (goal.getGoalkeeper() != null) {
                detail = detail + " (goalkeeper: " + goal.getGoalkeeper().getName() + ")";
            }
        } else if (incidence instanceof YellowCard card) {
            detail = card.getPlayer().getName();
        } else if (incidence instanceof Expulsion expulsion) {
            detail = expulsion.getPlayer().getName();
            if (expulsion.getReason() != null) {
                detail = detail + " (" + expulsion.getReason().getDescription() + ")";
            }
        } else if (incidence instanceof Change change) {
            detail = change.getPlayerOut().getName() + " -> " + change.getPlayerIn().getName();
        } else if (incidence instanceof PenaltyExecuted penalty) {
            detail = penalty.getKicker().getName() + (penalty.isScored() ? " scored" : " missed");
        } else {
            detail = incidence.getDescription();
        }
        return detail;
    }

    private static String refereeNameOf(Referee referee) {
        return referee == null ? NOT_AVAILABLE : referee.getName();
    }

    private static String stadiumNameOf(Stadium stadium) {
        return stadium == null ? NOT_AVAILABLE : stadium.getName();
    }
}
