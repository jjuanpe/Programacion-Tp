package controller;

import dao.CityDao;
import dao.StadiumDAO;
import model.common.Country;
import model.venue.City;
import model.venue.Stadium;

import java.sql.SQLException;
import java.util.List;

// Only place where city and stadium data goes to the database. Builds the domain objects too.
public class VenueCatalog {

    private final CityDao cityDao = new CityDao();
    private final StadiumDAO stadiumDao = new StadiumDAO();

    public List<City> getCities() throws SQLException {
        return cityDao.getAllCities();
    }

    public List<Stadium> getStadiums() throws SQLException {
        return stadiumDao.getAllStadiums();
    }

    public void addCity(long id, String name, String countryName) throws SQLException {
        cityDao.insertCity(id, name, new Country(countryName));
    }

    public void updateCity(long id, String name, String countryName) throws SQLException {
        cityDao.updateCity(id, name, new Country(countryName));
    }

    public void deleteCity(long id) throws SQLException {
        cityDao.deleteCity(id);
    }

    public void addStadium(long id, String name, long cityId, int capacity) throws SQLException {
        stadiumDao.insertStadium(id, name, findCity(cityId), capacity);
    }

    public void updateStadium(long originalId, long newId, String name, long cityId, int capacity)
            throws SQLException {
        stadiumDao.updateStadium(originalId, newId, name, findCity(cityId), capacity);
    }

    public void deleteStadium(long id) throws SQLException {
        stadiumDao.deleteStadium(id);
    }

    private City findCity(long cityId) throws SQLException {
        City city = cityDao.getCityById(cityId);
        if (city == null) {
            throw new IllegalArgumentException("The selected city does not exist.");
        }
        return city;
    }
}
