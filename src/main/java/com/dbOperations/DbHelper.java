package com.dbOperations;

import com.dbConnection.DatabaseConfig;

import java.sql.*;

public class DbHelper {

    private static Connection conn = null;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DatabaseConfig.getConnection();
        }
        return conn;
    }
}
