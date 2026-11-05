package controller;

import model.venue.City;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CitiesController {

    private final TournamentSession session;

    public CitiesController(TournamentSession session) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
    }

    public List<City> getRegisteredCities() throws SQLException {
        return session.getRegisteredCities();
    }

    public void addCity(long id, String name, String countryName) throws SQLException {
        session.addCity(id, name, countryName);
    }

    public void updateCity(long id, String name, String countryName) throws SQLException {
        session.updateCity(id, name, countryName);
    }

    public void deleteCity(City city) throws SQLException {
        session.deleteCity(city);
    }
}
