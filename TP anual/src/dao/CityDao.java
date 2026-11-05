package dao;

import model.team.Country;
import model.venue.City;
import model.venue.CityInUseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CityDao {

    public void insertCity(Long id, String name, Country country) throws SQLException {
        City city = new City(id, name, country);
        String query = "INSERT INTO ciudades (idciudad, nombre, pais) VALUES (?, ?, ?)";
        try (Connection cn = DBManager.getConnection();
             PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setLong(1, city.getId());
            pst.setString(2, city.getName());
            pst.setString(3, city.getCountry().getCountryName());
            pst.executeUpdate();
        }
    }

    public void updateCity(Long id, String name, Country country) throws SQLException {
        City city = new City(id, name, country);
        String query = "UPDATE ciudades SET nombre = ?, pais = ? WHERE idciudad = ?";
        try (Connection cn = DBManager.getConnection();
             PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, city.getName());
            pst.setString(2, city.getCountry().getCountryName());
            pst.setLong(3, city.getId());
            if (pst.executeUpdate() == 0) {
                throw new SQLException("The selected city no longer exists. Refresh the list.");
            }
        }
    }

    public void deleteCity(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("The city id must be positive");
        }
        try (Connection cn = DBManager.getConnection()) {
            cn.setAutoCommit(false);
            try {

                try (Statement lock = cn.createStatement()) {
                    lock.execute("LOCK TABLE estadios IN SHARE MODE");
                }
                String cityName;
                try (PreparedStatement cityQuery = cn.prepareStatement(
                        "SELECT nombre FROM ciudades WHERE idciudad = ? FOR UPDATE")) {
                    cityQuery.setLong(1, id);
                    try (ResultSet rs = cityQuery.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("The selected city no longer exists. Refresh the list.");
                        }
                        cityName = rs.getString("nombre");
                    }
                }
                try (PreparedStatement stadiumQuery = cn.prepareStatement(
                        "SELECT nombre FROM estadios WHERE idciudad = ? ORDER BY nombre, idestadio")) {
                    stadiumQuery.setLong(1, id);
                    try (ResultSet rs = stadiumQuery.executeQuery()) {
                        List<String> stadiumNames = new ArrayList<>();
                        while (rs.next()) {
                            stadiumNames.add(rs.getString("nombre"));
                        }
                        if (!stadiumNames.isEmpty()) {
                            throw new CityInUseException(cityName, stadiumNames);
                        }
                    }
                }
                try (PreparedStatement pst = cn.prepareStatement("DELETE FROM ciudades WHERE idciudad = ?")) {
                    pst.setLong(1, id);
                    pst.executeUpdate();
                }
                cn.commit();
            } catch (SQLException | RuntimeException exception) {
                cn.rollback();
                throw exception;
            }
        }
    }

    public List<City> getAllCities () throws SQLException{
        String query = "SELECT nombre,idciudad,pais FROM ciudades ORDER BY nombre, idciudad";
        List<City> cities = new ArrayList<>();

        try (Connection cn = DBManager.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(query);){
            while (rs.next()){
                String name = rs.getString("nombre");
                Long id = rs.getLong("idciudad");
                String countryName = rs.getString("pais");
                Country country = new Country(countryName);
                City city = new City(id,name,country);
                cities.add(city);
            }
        }
        return cities;
    }

    public City getCityById(Long id) throws SQLException{
        String query = "SELECT nombre,idciudad,pais FROM ciudades WHERE idciudad = ?";
        City cityfound = null;

        try (Connection cn = DBManager.getConnection();
        PreparedStatement pst = cn.prepareStatement(query);){
            pst.setLong(1,id);
            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()){
                    Long idciudad = rs.getLong("idciudad");
                    String name = rs.getString("nombre");
                    String countryName = rs.getString("pais");
                    Country country = new Country(countryName);
                    cityfound = new City(idciudad,name,country);
                }
            }
        }
        return cityfound;
    }
}
