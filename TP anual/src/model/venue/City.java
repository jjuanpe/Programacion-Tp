package model.venue;

import model.team.Country;

public class City {

    private Long id;
    private String name;
    private Country country;

    public City(Long id, String name, Country country) {
        this.id = id;
        this.name = name;
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

}