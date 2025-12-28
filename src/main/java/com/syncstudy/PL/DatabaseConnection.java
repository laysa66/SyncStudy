package com.syncstudy.PL;

import io.github.cdimascio.dotenv.Dotenv;
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
        // Load .env file from project root
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Try to get from .env first, then fall back to system environment variables
        this.dbUrl = getEnvVariable(dotenv, "DB_URL");
        this.dbUser = getEnvVariable(dotenv, "DB_USER");
        this.dbPassword = getEnvVariable(dotenv, "DB_PASSWORD");

        if (this.dbUrl == null || this.dbUser == null || this.dbPassword == null) {
            throw new RuntimeException("Database configuration not found. Please create a .env file with DB_URL, DB_USER, and DB_PASSWORD or set them as environment variables.");
        }

        System.out.println("Database Configuration:");
        System.out.println("URL: " + this.dbUrl);
        System.out.println("User: " + this.dbUser);
    }

    /**
     * Get environment variable from dotenv or system environment
     */
    private String getEnvVariable(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value;
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