package controller;

import model.venue.City;
import model.venue.Stadium;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class StadiumsController {

    private final TournamentSession session;

    public StadiumsController(TournamentSession session) {
        this.session = Objects.requireNonNull(session, "The tournament session is required");
    }

    public List<Stadium> getRegisteredStadiums() throws SQLException {
        return session.getRegisteredStadiums();
    }

    public List<City> getRegisteredCities() throws SQLException {
        return session.getRegisteredCities();
    }

    public void addStadium(long id, String name, City city, int capacity) throws SQLException {
        session.addStadium(id, name, city, capacity);
    }

    public void updateStadium(long originalId, long newId, String name, City city, int capacity)
            throws SQLException {
        session.updateStadium(originalId, newId, name, city, capacity);
    }

    public void deleteStadium(Stadium stadium) throws SQLException {
        session.deleteStadium(stadium);
    }
}
