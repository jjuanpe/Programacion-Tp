package model;

import java.util.Arrays;
import java.util.List;

public class Championship {
    private List<Team> teams;
    private List<Zone> zones;
    private List<Referee> referees;
    private List<Stadium> stadiums;

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
