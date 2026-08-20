package ui;

import model.competition.StandingEntry;
import model.event.Incidence;
import model.match.GroupMatch;
import model.simulation.MatchSimulationReport;

import java.util.List;
import java.util.Objects;

public class ConsoleReportFormatter {

    public String format(MatchSimulationReport report) {
        Objects.requireNonNull(report, "The simulation report is required");
        GroupMatch match = report.getMatch();
        StringBuilder result = new StringBuilder();

        result.append(System.lineSeparator())
                .append("Zone ").append(report.getZoneName())
                .append(" | ").append(match.getDate())
                .append(" | ").append(report.getWorkerThreadName())
                .append(System.lineSeparator())
                .append(match.getHomeTeam().getName())
                .append(' ').append(match.getHomeGoals())
                .append(" - ").append(match.getAwayGoals()).append(' ')
                .append(match.getAwayTeam().getName())
                .append(System.lineSeparator())
                .append("Referee: ").append(match.getReferee().getName())
                .append(System.lineSeparator())
                .append("Incidences:").append(System.lineSeparator());

        if (match.getIncidences().isEmpty()) {
            result.append("  No incidences").append(System.lineSeparator());
        } else {
            for (Incidence incidence : match.getIncidences()) {
                result.append("  ").append(incidence).append(System.lineSeparator());
            }
        }

        result.append("Standings before the result:").append(System.lineSeparator());
        appendStandings(result, report.getStandingsBefore());
        result.append("Standings after the result:").append(System.lineSeparator());
        appendStandings(result, report.getStandingsAfter());
        return result.toString();
    }

    public String formatFinalStandings(String zoneName, List<StandingEntry> standings) {
        Objects.requireNonNull(zoneName, "The zone name is required");
        Objects.requireNonNull(standings, "The standings are required");
        StringBuilder result = new StringBuilder();
        result.append(System.lineSeparator())
                .append("Final standings - Zone ").append(zoneName)
                .append(System.lineSeparator());
        appendStandings(result, standings);
        return result.toString();
    }

    private void appendStandings(StringBuilder result, List<StandingEntry> standings) {
        result.append(String.format(
                "%-24s %3s %3s %3s %3s %3s %3s %3s %4s %3s%n",
                "TEAM", "PTS", "P", "W", "D", "L", "GF", "GA", "GD", "FP"));
        for (StandingEntry entry : standings) {
            result.append(String.format(
                    "%-24s %3d %3d %3d %3d %3d %3d %3d %4d %3d%n",
                    entry.getTeam().getName(),
                    entry.getPoints(),
                    entry.getPlayed(),
                    entry.getWon(),
                    entry.getDrawn(),
                    entry.getLost(),
                    entry.getGoalsFor(),
                    entry.getGoalsAgainst(),
                    entry.getGoalDifference(),
                    entry.getFairPlayPoints()));
        }
    }
}
