package model.competition;

import model.event.Expulsion;
import model.event.ExpulsionReason;
import model.event.Incidence;
import model.event.YellowCard;
import model.match.GroupMatch;
import model.people.Player;
import model.team.Team;

import java.util.Objects;

public class FairPlayCalculator {
    private static final int YELLOW_CARD_POINTS = 1;
    private static final int SECOND_YELLOW_EXPULSION_ADDITIONAL_POINTS = 1;
    private static final int DIRECT_RED_CARD_POINTS = 4;

    public int calculate(Zone zone, Team team) {
        Objects.requireNonNull(zone, "The zone is required");
        Objects.requireNonNull(team, "The team is required");
        if (!zone.getTeams().contains(team)) {
            throw new IllegalArgumentException("The team does not belong to the zone");
        }

        int fairPlayPoints = 0;
        for (GroupMatch match : zone.getGroupMatches()) {
            if (match.isPlayed()) {
                for (Incidence incidence : match.getIncidences()) {
                    if (incidence instanceof YellowCard yellowCard
                            && belongsToTeam(yellowCard.getPlayer(), team)) {
                        fairPlayPoints += YELLOW_CARD_POINTS;
                    }
                    if (incidence instanceof Expulsion expulsion
                            && belongsToTeam(expulsion.getPlayer(), team)) {
                        fairPlayPoints += expulsion.getReason() == ExpulsionReason.SECOND_YELLOW_CARD
                                ? SECOND_YELLOW_EXPULSION_ADDITIONAL_POINTS
                                : DIRECT_RED_CARD_POINTS;
                    }
                }
            }
        }
        return fairPlayPoints;
    }

    private boolean belongsToTeam(Player player, Team team) {
        return team.getPlayers().contains(player);
    }
}
