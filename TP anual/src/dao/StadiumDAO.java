package dao;

import model.venue.City;
import model.venue.Stadium;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StadiumDAO {

    private final CityDao cityDao = new CityDao();

    public void insertStadium (Long id, String name, City city, int capacity) throws SQLException{
        String query = "INSERT INTO estadios (idestadio,idciudad, nombre,capacidad) VALUES (?,?,?,?)";

        try(Connection cn = DBManager.getConnection();
            PreparedStatement pst = cn.prepareStatement(query);
        ){
            pst.setLong(1,id);
            pst.setLong(2,city.getId());
            pst.setString(3,name);
            pst.setInt(4,capacity);
            pst.executeUpdate();
        }
    }

    public void deleteStadium (Long id) throws SQLException{
        String query = "DELETE FROM estadios WHERE idestadio = ?";

        try (Connection cn = DBManager.getConnection();
        PreparedStatement pst = cn.prepareStatement(query);
        ){
            pst.setLong(1,id);
            pst.executeUpdate();
        }
    }

    public void updateStadium (Long id, int capacity) throws SQLException{
        String query = "UPDATE estadios SET capacidad = ? WHERE idestadio = ?";

        try(Connection cn = DBManager.getConnection();
            PreparedStatement pst = cn.prepareStatement(query);
        ){
            pst.setInt(1,capacity);
            pst.setLong(2,id);
            pst.executeUpdate();
        }
    }

    public List<Stadium> getAllStadiums () throws SQLException{
        Map<Long, City> citiesById = new HashMap<>();
        for (City city : cityDao.getAllCities()) {
            citiesById.put(city.getId(), city);
        }
        String query = "SELECT idestadio, idciudad, nombre, capacidad FROM estadios ORDER BY idestadio";
        List<Stadium> stadiumList = new ArrayList<>();

        try (Connection cn = DBManager.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(query);){
            while (rs.next()){
                Long id = rs.getLong("idestadio");
                Long idciudad = rs.getLong("idciudad");
                String name = rs.getString("nombre");
                int cap = rs.getInt("capacidad");
                City city = citiesById.get(idciudad);
                if (city == null) {
                    throw new SQLException("Stadium " + id + " refers to an unknown city: " + idciudad);
                }
                Stadium stadium = new Stadium(id,name,city,cap);
                stadiumList.add(stadium);
            }
        }
        return stadiumList;
    }
}
