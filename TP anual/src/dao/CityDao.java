package dao;

import model.team.Country;
import model.venue.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CityDao {

    public void insertCity (Long id , String name) throws SQLException {
        String query = "INSERT INTO ciudades (nombre,idciudad) VALUES (?,?)";

        try(Connection cn = DBManager.getConnection();
            PreparedStatement pst = cn.prepareStatement(query);
            ){
                pst.setString(1,name);
                pst.setLong(2,id);
                pst.executeUpdate();
        }
    }

    public void deleteCity (Long id) throws SQLException{
        String query = "DELETE FROM ciudades WHERE idciudad = ?";

        try(Connection cn = DBManager.getConnection();
        PreparedStatement pst = cn.prepareStatement(query);
        ){
            pst.setLong(1,id);
            pst.executeUpdate();
        }
    }

    // public void updateCity () .. Que se actualiza de las ciudades ???

    public List<City> getAllCities () throws SQLException{
        String query = "SELECT nombre,idciudad FROM ciudades";
        List<City> cities = new ArrayList<>();

        try (Connection cn = DBManager.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(query);){
            while (rs.next()){
                String name = rs.getString("nombre");
                Long id = rs.getLong("idciudad");
                City city = new City(id,name,new Country("Argentina"));
                cities.add(city);
            }
        }
        return cities;
    }

    public City getCityById(Long id) throws SQLException{
        String query = "SELECT nombre,idciudad FROM ciudades WHERE idciudad = ?";
        City cityfound = null;

        try (Connection cn = DBManager.getConnection();
        PreparedStatement pst = cn.prepareStatement(query);){
            pst.setLong(1,id);
            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()){
                    Long idciudad = rs.getLong("idciudad");
                    String name = rs.getString("nombre");
                    cityfound = new City(idciudad,name,new Country("Argentina"));
                }
            }
        }
        return cityfound;
    }
}
