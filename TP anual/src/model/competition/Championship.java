package model.competition;

import model.venue.Stadium;
import model.people.Referee;
import model.team.Team;

import java.util.List;
import java.util.Objects;

public class Championship {
    private final List<Team> teams;
    private final List<Zone> zones;
    private final List<Referee> referees;
    private final List<Stadium> stadiums;

    public Championship(
            List<Team> teams,
            List<Zone> zones,
            List<Referee> referees,
            List<Stadium> stadiums) {
        Objects.requireNonNull(teams, "The team list is required");
        Objects.requireNonNull(zones, "The zone list is required");
        Objects.requireNonNull(referees, "The referee list is required");
        Objects.requireNonNull(stadiums, "The stadium list is required");
        this.teams = List.copyOf(teams);
        this.zones = List.copyOf(zones);
        this.referees = List.copyOf(referees);
        this.stadiums = List.copyOf(stadiums);
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
