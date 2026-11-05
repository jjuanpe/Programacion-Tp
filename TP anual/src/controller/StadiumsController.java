package controller;

import model.venue.City;
import model.venue.Stadium;
import viewmodel.CityRow;
import viewmodel.StadiumRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StadiumsController {

    private final VenueCatalog catalog = new VenueCatalog();

    public List<StadiumRow> getRegisteredStadiums() throws SQLException {
        List<StadiumRow> rows = new ArrayList<>();
        for (Stadium stadium : catalog.getStadiums()) {
            rows.add(new StadiumRow(stadium.getId(), stadium.getName(), stadium.getCity().getId(),
                    stadium.getCity().getName(), stadium.getCapacity()));
        }
        return rows;
    }

    public List<CityRow> getRegisteredCities() throws SQLException {
        List<CityRow> rows = new ArrayList<>();
        for (City city : catalog.getCities()) {
            rows.add(new CityRow(city.getId(), city.getName(), city.getCountry().getCountryName()));
        }
        return rows;
    }

    public void addStadium(long id, String name, long cityId, int capacity) throws SQLException {
        catalog.addStadium(id, name, cityId, capacity);
    }

    public void updateStadium(long originalId, long newId, String name, long cityId, int capacity)
            throws SQLException {
        catalog.updateStadium(originalId, newId, name, cityId, capacity);
    }

    public void deleteStadium(long id) throws SQLException {
        catalog.deleteStadium(id);
    }
}
