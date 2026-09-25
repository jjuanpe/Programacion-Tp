package model.venue;

import java.io.Serializable;
import model.team.Country;

import java.util.ArrayList;
import java.util.List;

public class City implements Serializable {
    private Long id;
    private String name;
    private Country country;
    private List<Stadium> stadiums = new ArrayList<>();

    public City(Long id, String name, Country country) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("The city id must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The city name is required");
        }
        if (country == null) {
            throw new IllegalArgumentException("The city country is required");
        }
        this.id = id;
        this.name = name.trim();
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public List<Stadium> getStadiums() {
        return List.copyOf(stadiums);
    }

    public void add (Stadium stadium){
        if (stadium == null || stadium.getCity() != this) {
            throw new IllegalArgumentException("The stadium must belong to this city");
        }
        if (!stadiums.contains(stadium)) {
            stadiums.add(stadium);
        }
    }

    public boolean remove(Stadium stadium) {
        return stadiums.remove(stadium);
    }

}
