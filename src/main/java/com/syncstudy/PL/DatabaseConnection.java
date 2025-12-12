package com.syncstudy.PL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database Connection Manager
 * Manages database connections without exposing DB-specific details
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    private DatabaseConnection() {
        this.dbUrl = "jdbc:postgresql://localhost:5432/suncstudydb";
        this.dbUser = "postgres";
        this.dbPassword = "postgres";
    }

    /**
     * Get the singleton instance
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }
    }

    /**
     * Set database configuration
     * @param dbUrl database URL
     * @param dbUser database user
     * @param dbPassword database password
     */
    public void setConfiguration(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }
}