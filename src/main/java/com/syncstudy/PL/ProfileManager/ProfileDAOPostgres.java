package com.syncstudy.PL.ProfileManager;

import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.PL.DatabaseConnection;

import java.sql.*;
import java.util.List;

/**
 * PostgreSQL implementation of ProfileDAO
 */
public class ProfileDAOPostgres {
    private DatabaseConnection dbConnection;

    public ProfileDAOPostgres() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    /**
     * Initialize database tables if they don't exist
     */
    private void initializeDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            createTableProfiles(conn);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Create profiles table if it doesn't exist
     * @param conn database connection
     */
    public void createTableProfiles(Connection conn) {
        // Create base table
        String sql = "CREATE TABLE IF NOT EXISTS profiles (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id SERIAL, " +
                "firstname VARCHAR(255) NOT NULL, " +
                "lastname VARCHAR(255) NOT NULL)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'profiles' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }
    /**
     * Updates a profile with the given credentials
     *
     * @param conn database connection
     * @param profileId the profile id to search for
     * @param userId    the user id to search for
     * @param firstname the firstname to update
     * @param lastname  the lastname to update
     * @return true if update went well, false otherwise
     */
    public boolean updateProfile(Connection conn, Long profileId, Long userId, String firstname, String lastname) {
        String sql = "UPDATE profiles SET (firstname, lastname) VALUES (?, ?) WHERE id=? AND user_id=?" +
                "ON CONFLICT (user_id) DO NOTHING";
        boolean ok = false;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstname);
            pstmt.setString(2, lastname);
            pstmt.setLong(3, profileId);
            pstmt.setLong(4, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Profile '" + profileId + "' updated successfully.");
                ok = true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
        return ok;
    }

    /**
     * Find a profile by user id
     * @param userId the user id to search for
     * @return a UserProfile object if found, null otherwise
     */
    public UserProfile findProfileByUserId(Long userId) {
        String sql = "SELECT id, user_id, firstname, lastname FROM profiles WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();
                    profile.setId(rs.getLong("id"));
                    profile.setUserId(rs.getString("user_id"));
                    profile.setFirstname(rs.getString("firstname"));
                    profile.setLastname(rs.getString("lastname"));
                    return profile;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding profile: " + e.getMessage());
        }

        return null;
    }

    public List<UserProfile> findAllProfiles(Long excludeUserId) {
        //find how they did the search thing because it's exactly what it's supposed to be
    }
}
