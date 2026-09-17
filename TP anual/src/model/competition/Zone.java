package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Zone {
    private final List<Team> teams;
    private final List<GroupMatch> groupMatches;
    private final String name;

    public Zone(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The zone name is required");
        }
        this.teams = new ArrayList<>();
        this.groupMatches = new ArrayList<>();
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public List<Team> getTeams() {
        return List.copyOf(teams);
    }

    public List<GroupMatch> getGroupMatches() {
        return List.copyOf(groupMatches);
    }

    void addTeam(Team team) {
        Objects.requireNonNull(team, "The team is required");
        if (teams.contains(team)) {
            throw new IllegalArgumentException("A team cannot be added twice to a zone");
        }
        teams.add(team);
    }

    void addGroupMatches(List<GroupMatch> matches) {
        Objects.requireNonNull(matches, "The match list is required");
        groupMatches.addAll(List.copyOf(matches));
    }
}
