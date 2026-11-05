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

    public void insertStadium(Long id, String name, City city, int capacity) throws SQLException {
        validateInput(id, name, city, capacity);
        String query = "INSERT INTO estadios (idestadio, idciudad, nombre, capacidad) VALUES (?, ?, ?, ?)";
        try (Connection cn = DBManager.getConnection()) {
            cn.setAutoCommit(false);
            try {
                lockStadiumWrites(cn);
                validateCity(cn, city.getId());
                validateAvailableId(cn, id);
                try (PreparedStatement pst = cn.prepareStatement(query)) {
                    pst.setLong(1, id);
                    pst.setLong(2, city.getId());
                    pst.setString(3, name.trim());
                    pst.setInt(4, capacity);
                    pst.executeUpdate();
                }
                cn.commit();
            } catch (SQLException exception) {
                cn.rollback();
                throw describeConstraintError(exception, id);
            }
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

    public void updateStadium(Long originalId, Long newId, String name, City city, int capacity)
            throws SQLException {
        if (originalId == null || originalId <= 0) {
            throw new IllegalArgumentException("El ID original del estadio debe ser positivo.");
        }
        validateInput(newId, name, city, capacity);
        String query = "UPDATE estadios SET idestadio = ?, idciudad = ?, nombre = ?, capacidad = ? WHERE idestadio = ?";
        try (Connection cn = DBManager.getConnection()) {
            cn.setAutoCommit(false);
            try {
                lockStadiumWrites(cn);
                validateCity(cn, city.getId());
                if (!originalId.equals(newId)) {
                    validateAvailableId(cn, newId);
                }
                try (PreparedStatement pst = cn.prepareStatement(query)) {
                    pst.setLong(1, newId);
                    pst.setLong(2, city.getId());
                    pst.setString(3, name.trim());
                    pst.setInt(4, capacity);
                    pst.setLong(5, originalId);
                    if (pst.executeUpdate() == 0) {
                        throw new SQLException("El estadio seleccionado ya no existe. Actualizá el listado.");
                    }
                }
                cn.commit();
            } catch (SQLException exception) {
                cn.rollback();
                throw describeConstraintError(exception, newId);
            }
        }
    }

    private void validateInput(Long id, String name, City city, int capacity) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del estadio debe ser un número entero positivo.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del estadio no puede estar vacío.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        if (city == null) {
            throw new IllegalArgumentException("Seleccioná una ciudad existente.");
        }
    }

    private void lockStadiumWrites(Connection cn) throws SQLException {

        try (Statement lock = cn.createStatement()) {
            lock.execute("LOCK TABLE estadios IN ROW EXCLUSIVE MODE");
        }
    }

    private void validateCity(Connection cn, Long cityId) throws SQLException {
        try (PreparedStatement pst = cn.prepareStatement("SELECT idciudad FROM ciudades WHERE idciudad = ? FOR KEY SHARE")) {
            pst.setLong(1, cityId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La ciudad seleccionada ya no existe. Actualizá el listado de ciudades.");
                }
            }
        }
    }

    private void validateAvailableId(Connection cn, Long id) throws SQLException {
        try (PreparedStatement pst = cn.prepareStatement("SELECT idestadio FROM estadios WHERE idestadio = ?")) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    throw new SQLException("Ya existe un estadio con el ID " + id + ". Elegí otro ID.");
                }
            }
        }
    }

    private SQLException describeConstraintError(SQLException exception, Long id) {
        SQLException result = exception;
        if ("23505".equals(exception.getSQLState())) {
            result = new SQLException("Ya existe un estadio con el ID " + id
                    + " u otro dato único repetido. Revisá los datos ingresados.", exception.getSQLState(), exception);
        } else if ("23503".equals(exception.getSQLState())) {
            result = new SQLException("No se pudo guardar: la ciudad ya no existe o el ID del estadio "
                    + "está referenciado por otros registros.", exception.getSQLState(), exception);
        }
        return result;
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
