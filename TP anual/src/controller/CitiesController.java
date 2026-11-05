package controller;

import model.venue.City;
import viewmodel.CityRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CitiesController {

    private final VenueCatalog catalog = new VenueCatalog();

    public List<CityRow> getRegisteredCities() throws SQLException {
        List<CityRow> rows = new ArrayList<>();
        for (City city : catalog.getCities()) {
            rows.add(new CityRow(city.getId(), city.getName(), city.getCountry().getCountryName()));
        }
        return rows;
    }

    public void addCity(long id, String name, String countryName) throws SQLException {
        catalog.addCity(id, name, countryName);
    }

    public void updateCity(long id, String name, String countryName) throws SQLException {
        catalog.updateCity(id, name, countryName);
    }

    public void deleteCity(long id) throws SQLException {
        catalog.deleteCity(id);
    }
}
