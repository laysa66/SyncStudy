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
        // Create base table
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

        // Add admin columns if they don't exist
        extendUsersTable(conn);
    }

    /**
     * Extend users table with admin-related columns
     */
    private void extendUsersTable(Connection conn) {
        String[] alterStatements = {
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS university VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS department VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_photo VARCHAR(500)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS is_blocked BOOLEAN DEFAULT FALSE",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS is_admin BOOLEAN DEFAULT FALSE",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS registration_date TIMESTAMP DEFAULT NOW()",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login TIMESTAMP"
        };

        for (String sql : alterStatements) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                // Column might already exist or other issue - continue
                System.err.println("Note: " + e.getMessage());
            }
        }
        System.out.println("Users table extended with admin columns.");
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
            // Check if admin exists using simple query (only base columns)
            String checkSql = "SELECT id FROM users WHERE username = 'admin'";
            boolean adminExists = false;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSql)) {
                adminExists = rs.next();
            }

            if (!adminExists) {
                String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                String insertSql = "INSERT INTO users (username, password_hash, is_admin) VALUES (?, ?, TRUE)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, hashedPassword);
                    pstmt.executeUpdate();
                    System.out.println("Default admin user created.");
                }
            } else {
                // Reset admin password and ensure is_admin = TRUE
                String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                String updateSql = "UPDATE users SET password_hash = ?, is_admin = TRUE WHERE username = 'admin'";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, hashedPassword);
                    pstmt.executeUpdate();
                    System.out.println("Admin password reset to 'admin123'.");
                }
            }

            // Create test regular users
            //todo : this only for me to test admin features, remove later
            createTestUserIfNotExists(conn, "laysa.matmar", "password123", "Laysa Matmar", "lm@university.edu", "polytech montpellier", "Computer Science");
            createTestUserIfNotExists(conn, "omar.hussein", "password123", "omar hussein Smith", "omar.smith@university.edu", "Stanford", "logics");
            createTestUserIfNotExists(conn, "bob.recardo", "password123", "Bob Recardo Tokyo", "tokyo@university.edu", "Harvard", "Physics");
            createTestUserIfNotExists(conn, "alice.wonder", "password123", "Alice Wonder", "alice@university.com", "MIT", "Mathematics");

        } catch (Exception e) {
            System.err.println("Error creating default user: " + e.getMessage());
        }
    }

    /**
     * Create a test user if not exists
     */
    private void createTestUserIfNotExists(Connection conn, String username, String password,
            String fullName, String email, String university, String department) {
        try {
            String checkSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return; // User already exists
                    }
                }
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String insertSql = "INSERT INTO users (username, password_hash, full_name, email, university, department, is_admin, is_blocked) " +
                    "VALUES (?, ?, ?, ?, ?, ?, FALSE, FALSE)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.setString(3, fullName);
                pstmt.setString(4, email);
                pstmt.setString(5, university);
                pstmt.setString(6, department);
                pstmt.executeUpdate();
                System.out.println("Test user '" + username + "' created.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating test user " + username + ": " + e.getMessage());
        }
    }

    @Override
    public User findUserByUsername(String username) {
        // Use columns that should exist after table extension
        String sql = "SELECT id, username, password_hash, " +
                "COALESCE(email, '') as email, " +
                "COALESCE(full_name, '') as full_name, " +
                "COALESCE(is_blocked, FALSE) as is_blocked, " +
                "COALESCE(is_admin, FALSE) as is_admin " +
                "FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setBlocked(rs.getBoolean("is_blocked"));
                    user.setAdmin(rs.getBoolean("is_admin"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
            // Fallback: try with basic columns only
            return findUserByUsernameBasic(username);
        }

        return null;
    }

    /**
     * Fallback method using only basic columns
     */
    private User findUserByUsernameBasic(String username) {
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