package model.competition;

import model.match.Match;
import model.people.HeadCoach;
import model.people.Player;
import model.team.Country;
import model.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TeamReportService {

    private static final Comparator<TeamReportEntry> ALPHABETICAL =
            Comparator.comparing(entry -> entry.getTeam().getName(), String.CASE_INSENSITIVE_ORDER);

    public List<TeamReportEntry> computeReport(
            List<Team> teams,
            List<? extends Match> matches,
            LocalDate referenceDate) {
        Objects.requireNonNull(teams, "The team list is required");
        Objects.requireNonNull(matches, "The match list is required");
        Objects.requireNonNull(referenceDate, "The reference date is required");

        Map<Team, TeamReportEntry> entries = new LinkedHashMap<>();
        for (Team team : teams) {
            entries.put(team, buildEntry(team, referenceDate));
        }

        for (Match match : matches) {
            if (match.isPlayed()) {
                registerMatch(match, entries);
            }
        }

        List<TeamReportEntry> report = new ArrayList<>(entries.values());
        report.sort(ALPHABETICAL);
        return report;
    }

    private TeamReportEntry buildEntry(Team team, LocalDate referenceDate) {
        HeadCoach coach = team.getHeadCoach();
        int coachAge = coach == null ? 0 : coach.getAge(referenceDate);
        Country coachCountry = coach == null ? null : coach.getCountry();
        return new TeamReportEntry(
                team, averagePlayerAge(team, referenceDate), coachAge, coachCountry);
    }

    private double averagePlayerAge(Team team, LocalDate referenceDate) {
        List<Player> players = team.getPlayers();
        double average = 0;
        if (!players.isEmpty()) {
            int total = 0;
            for (Player player : players) {
                total += player.getAge(referenceDate);
            }
            average = (double) total / players.size();
        }
        return average;
    }

    private void registerMatch(Match match, Map<Team, TeamReportEntry> entries) {
        TeamReportEntry home = entries.get(match.getHomeTeam());
        TeamReportEntry away = entries.get(match.getAwayTeam());
        if (home != null) {
            home.registerMatch(match.getHomeGoals(), match.getAwayGoals());
        }
        if (away != null) {
            away.registerMatch(match.getAwayGoals(), match.getHomeGoals());
        }
    }
}
