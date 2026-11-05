package model.competition;

import model.team.Team;

import java.util.Objects;

public class FairPlayEntry {

    private final Team team;
    private int position;
    private int yellowCards;
    private int expulsions;
    private int points;

    FairPlayEntry(Team team) {
        this.team = Objects.requireNonNull(team, "The team is required");
    }

    public void registerYellowCard() {
        yellowCards++;
    }

    public void registerExpulsion() {
        expulsions++;
    }

    void setPoints(int points) {
        this.points = points;
    }

    void setPosition(int position) {
        if (position <= 0) {
            throw new IllegalArgumentException("The ranking position must be positive");
        }
        this.position = position;
    }

    public Team getTeam() { return team; }

    public int getPosition() { return position; }

    public int getYellowCards() { return yellowCards; }

    public int getExpulsions() { return expulsions; }

    public int getPoints() { return points; }
}