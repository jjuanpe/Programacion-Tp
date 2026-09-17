package dao;

import java.sql.*;

public class DBManager {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/tpprogra";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "1234";

    public static Connection getConnection() throws SQLException{
        String url = System.getenv().getOrDefault("TP_DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("TP_DB_USER", DEFAULT_USER);
        String password = System.getenv().getOrDefault("TP_DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }
}
