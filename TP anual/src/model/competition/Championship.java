package model.competition;

import model.venue.Stadium;
import model.people.Referee;
import model.team.Team;

import java.util.ArrayList;
import java.util.List;

public class Championship {
    private List<Team> teams;
    private List<Zone> zones;
    private List<Referee> referees;
    private List<Stadium> stadiums;

    // TODO Sprint 3: add domain methods to store the drawn zones and their generated fixtures.
    // TODO Sprint 3: keep the assigned referees and the tournament progress in this class.
    // TODO Future sprint: include the persistence state when the database module is introduced.

    public Championship() {
        this.teams = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.referees = new ArrayList<>();
        this.stadiums = new ArrayList<>();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public List<Stadium> getStadiums() {
        return stadiums;
    }
}
