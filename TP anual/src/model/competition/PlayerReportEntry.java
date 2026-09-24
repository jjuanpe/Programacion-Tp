package model.competition;

import model.people.Player;
import model.team.Team;

import java.util.Objects;

public class PlayerReportEntry {

    private final Player player;
    private final Team team;
    private int matchesPlayed;
    private int minutesPlayed;
    private int goals;
    private int goalsConceded;

    PlayerReportEntry(Player player, Team team) {
        this.player = Objects.requireNonNull(player, "The player is required");
        this.team = team;
    }

    void registerMatch(int minutesPlayedInMatch) {
        matchesPlayed++;
        minutesPlayed += minutesPlayedInMatch;
    }

    void registerGoal() {
        goals++;
    }

    void registerConceded(int goalsConcededInMatch) {
        goalsConceded += goalsConcededInMatch;
    }

    public Player getPlayer() { return player; }

    public Team getTeam() { return team; }

    public int getMatchesPlayed() { return matchesPlayed; }

    public int getMinutesPlayed() { return minutesPlayed; }

    public int getGoals() { return goals; }

    public int getGoalsConceded() { return goalsConceded; }

    public double getGoalsConcededAverage() {
        return matchesPlayed == 0 ? 0 : (double) goalsConceded / matchesPlayed;
    }
}
