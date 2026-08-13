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
