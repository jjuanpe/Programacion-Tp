package model.competition;

import java.io.Serializable;
import model.venue.Stadium;
import model.venue.City;
import model.people.Referee;
import model.team.Team;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Championship implements Serializable {
    private final List<Team> teams;
    private final List<Zone> zones;
    private final List<Referee> referees;
    private final List<Stadium> stadiums;
    private final List<City> cities;

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
        this.cities = new ArrayList<>();
        for (Stadium stadium : this.stadiums) {
            City city = stadium.getCity();
            if (!cities.contains(city)) {
                cities.add(city);
            }
            city.add(stadium);
        }
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

    public List<City> getCities() {
        return List.copyOf(cities);
    }

    public void consumeStadium(Stadium stadium) {
        City city = Objects.requireNonNull(stadium, "The stadium is required").getCity();
        if (!city.remove(stadium)) {
            throw new IllegalStateException("The stadium is not available in its city");
        }
        if (city.getStadiums().isEmpty()) {
            cities.remove(city);
        }
    }
}
