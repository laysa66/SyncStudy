package com.syncstudy.PL.ProfileManager;

import com.syncstudy.BL.ProfileManager.ProfileDAO;
import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.PL.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL implementation of ProfileDAO
 */
public class ProfileDAOPostgres extends ProfileDAO {
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
            //bind test profiles to the test users ?
            createTestProfile(1L,"Admin","Admin");
            createTestProfile(8L,"Alice","Wonder");
            createTestProfile(5L,"Laysa","Matmar");
            createTestProfile(6L,"Omar hussein","Smith");
            createTestProfile(7L,"Bob recardo","Tokyo");
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
     * Inserts a new profile in the database with given credentials and ensures it has been inserted
     * @param userId provided user id
     * @param firstname provided firstname
     * @param lastname provided lastname
     * @return profile id
     */
    public Long createProfile(Long userId, String firstname, String lastname) {
        String sql = "INSERT INTO profiles (user_id, firstname, lastname) VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id) DO NOTHING";

        try (Connection conn = this.dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, firstname);
            pstmt.setString(3, lastname);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Profile for user '" + userId + "' inserted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting profile: " + e.getMessage());
        }

        String sql2 = "SELECT id FROM profiles WHERE user_id=?";
        try (Connection conn = this.dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying profile registration: " + e.getMessage());
        }

        return null;

    }

    /**
     * Updates a profile with the given credentials
     * @param profileId the profile id to search for
     * @param userId    the user id to search for
     * @param firstname the firstname to update
     * @param lastname  the lastname to update
     * @return true if update went well, false otherwise
     */
    public boolean updateProfile(Long profileId, Long userId, String firstname, String lastname) {
        String sql = "UPDATE profiles SET (firstname, lastname) VALUES (?, ?) WHERE id=? AND user_id=?" +
                "ON CONFLICT (user_id) DO NOTHING";
        boolean ok = false;
        try (Connection conn = this.dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
                    profile.setUserId(rs.getLong("user_id"));
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

    public List<UserProfile> findAllProfiles(String searchQuery, String sortBy, int page, int pageSize) {
        List<UserProfile> profiles = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, user_id, firstname, lastname, full_name " +
                        "FROM profiles WHERE 1=1 "
        );

        // Add search filter
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append("AND (LOWER(firstname) LIKE LOWER(?) OR LOWER(lastname) LIKE LOWER(?)) ");
        }

        // Add sorting
        switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "firstname":
                sql.append("ORDER BY firstname ASC ");
                break;
            case "lastname":
                sql.append("ORDER BY lastname ASC ");
                break;
            case "name":
            default:
                sql.append("ORDER BY firstname ASC, lastname ASC "); // Sort by both names
                break;
        }

        // Add pagination
        sql.append("LIMIT ? OFFSET ?");

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set search parameters
            if (searchQuery != null && !searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern); // Only 2 parameters now
            }

            // Set pagination parameters
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, page * pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToUserProfile(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all profiles: " + e.getMessage());
        }

        return profiles;
    }

    @Override
    public boolean deleteProfile(Long userId) {
        String sql = "DELETE FROM profiles WHERE user_id=?";
        boolean ok = false;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ok = true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting profile: " + e.getMessage());
        }
        return ok;
    }

    @Override
    public int getTotalProfilesCount(String searchQuery) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM profiles WHERE 1=1 ");

        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append("AND (LOWER(firstname) LIKE LOWER(?) OR LOWER(lastname) LIKE LOWER(?)");
        }

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            if (searchQuery != null && !searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting profiles count: " + e.getMessage());
        }

        return 0;
    }


    private UserProfile mapResultSetToUserProfile(ResultSet rs) throws SQLException {
        UserProfile profile = new UserProfile();
        profile.setId(rs.getLong("id"));
        profile.setUserId(rs.getLong("user_id"));
        profile.setFirstname(rs.getString("firstname"));
        profile.setLastname(rs.getString("lastname"));
        return profile;
    }

    private void createTestProfile(Long userId, String firstname, String lastname) {
        try {
            String checkSql = "SELECT id FROM profile WHERE user_id = ?";
            try (Connection conn = this.dbConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setLong(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return; // Profile already exists
                    }
                }
            }

            Long id = createProfile(userId,firstname,lastname);
        } catch (SQLException e) {
            System.err.println("Error creating test profile for user " + userId + ": " + e.getMessage());
        }
    }

}
