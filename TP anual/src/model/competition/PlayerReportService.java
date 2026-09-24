package model.competition;

import model.event.Goal;
import model.event.Incidence;
import model.match.Match;
import model.match.PlayerParticipation;
import model.people.Player;
import model.people.Position;
import model.team.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayerReportService {

    private static final Comparator<PlayerReportEntry> ALPHABETICAL =
            Comparator.<PlayerReportEntry, String>comparing(
                            entry -> entry.getTeam().getName(), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(entry -> entry.getPlayer().getName(), String.CASE_INSENSITIVE_ORDER);

    public List<PlayerReportEntry> computeReport(List<Team> teams, List<? extends Match> matches) {
        Objects.requireNonNull(teams, "The team list is required");
        Objects.requireNonNull(matches, "The match list is required");

        Map<Player, PlayerReportEntry> entries = new LinkedHashMap<>();
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                entries.put(player, new PlayerReportEntry(player, team));
            }
        }

        for (Match match : matches) {
            if (match.isPlayed()) {
                registerMatch(match, entries);
            }
        }

        List<PlayerReportEntry> report = new ArrayList<>(entries.values());
        report.sort(ALPHABETICAL);
        return report;
    }

    private void registerMatch(Match match, Map<Player, PlayerReportEntry> entries) {
        for (PlayerParticipation participation : match.getParticipations()) {
            if (participation.getMinutesPlayed() > 0) {
                PlayerReportEntry entry = entries.get(participation.getPlayer());
                if (entry != null) {
                    entry.registerMatch(participation.getMinutesPlayed());
                    if (entry.getPlayer().getPosition() == Position.GOALKEEPER) {
                        entry.registerConceded(concededGoals(match, entry.getTeam()));
                    }
                }
            }
        }
        for (Incidence incidence : match.getIncidences()) {
            if (incidence instanceof Goal goal && !goal.isOwnGoal()) {
                PlayerReportEntry entry = entries.get(goal.getScorer());
                if (entry != null) {
                    entry.registerGoal();
                }
            }
        }
    }

    private int concededGoals(Match match, Team team) {
        return match.getHomeTeam() == team ? match.getAwayGoals() : match.getHomeGoals();
    }
}
