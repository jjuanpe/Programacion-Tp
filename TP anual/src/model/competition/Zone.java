package model.competition;

import model.match.GroupMatch;
import model.team.Team;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private List<Team> teams;
    private List<GroupMatch> groupMatches;

    public Zone() {
        this.teams = new ArrayList<>();
        this.groupMatches = new ArrayList<>();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<GroupMatch> getGroupMatches() {
        return groupMatches;
    }
}
