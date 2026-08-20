package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private List<Team> teams;
    private List<GroupMatch> groupMatches;
    private String name;

    public Zone(String name) {
        this.teams = new ArrayList<>();
        this.groupMatches = new ArrayList<>();
        this.name= name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<GroupMatch> getGroupMatches() {
        return groupMatches;
    }
}
