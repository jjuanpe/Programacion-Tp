package dataload;

import java.io.Serializable;
import model.people.Referee;
import model.team.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TournamentData implements Serializable {
    private final List<Team> teams = new ArrayList<>();
    private final List<Referee> referees = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void addReferee(Referee referee) {
        referees.add(referee);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public List<Referee> getReferees() {
        return Collections.unmodifiableList(referees);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}