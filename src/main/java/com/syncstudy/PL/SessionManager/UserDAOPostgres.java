package com.syncstudy.PL.SessionManager;

import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.BL.SessionManager.UserDAO;
import com.syncstudy.PL.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PostgreSQL implementation of UserDAO
 */
public class UserDAOPostgres extends UserDAO {

    private DatabaseConnection dbConnection;

    public UserDAOPostgres() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    /**
     * Initialize database tables if they don't exist
     */
    private void initializeDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            createTableUsers(conn);
            // Insert default user for testing (username: admin, password: admin123)
            insertDefaultUserIfNotExists(conn);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Create users table if it doesn't exist
     * @param conn database connection
     */
    public void createTableUsers(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(100) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'users' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Insert a user into the database
     * @param conn database connection
     * @param username the username
     * @param passwordHash the hashed password
     */
    public void insertUser(Connection conn, String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) " +
                "ON CONFLICT (username) DO NOTHING";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User '" + username + "' inserted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
    }

    /**
     * Insert default test user if database is empty
     */
    private void insertDefaultUserIfNotExists(Connection conn) {
        try {
            User existingUser = findUserByUsername("admin");
            if (existingUser == null) {
                String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                insertUser(conn, "admin", hashedPassword);
            }
        } catch (Exception e) {
            System.err.println("Error creating default user: " + e.getMessage());
        }
    }

    @Override
    public User findUserByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        User user = findUserByUsername(username);

        if (user == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(password, user.getPasswordHash());
        } catch (Exception e) {
            System.err.println("Error checking password: " + e.getMessage());
            return false;
        }
    }
}