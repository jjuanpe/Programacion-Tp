package model.competition;

import model.people.Player;
import model.team.Team;

import java.util.Objects;

public class ScorerEntry {

    private final Player player;
    private final Team team;
    private int position;
    private int goals;
    private int penaltyGoals;

    ScorerEntry(Player player, Team team) {
        this.player = Objects.requireNonNull(player, "The player is required");
        this.team = team;
    }

    void registerGoal(boolean penalty) {
        goals++;
        if (penalty) {
            penaltyGoals++;
        }
    }

    void setPosition(int position) {
        if (position <= 0) {
            throw new IllegalArgumentException("The ranking position must be positive");
        }
        this.position = position;
    }

    public Player getPlayer() { return player; }

    public Team getTeam() { return team; }

    public int getPosition() { return position; }

    public int getGoals() { return goals; }

    public int getPenaltyGoals() { return penaltyGoals; }
}
