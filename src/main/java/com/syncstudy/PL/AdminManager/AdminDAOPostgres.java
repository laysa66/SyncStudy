package com.syncstudy.PL.AdminManager;

import com.syncstudy.BL.AdminManager.AdminDAO;
import com.syncstudy.BL.AdminManager.BlockRecord;
import com.syncstudy.BL.AdminManager.UserActivity;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.PL.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL implementation of AdminDAO
 * Handles all admin-related database operations
 */
public class AdminDAOPostgres extends AdminDAO {

    private DatabaseConnection dbConnection;

    public AdminDAOPostgres() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeAdminTables();
    }

    /**
     * Initialize admin-related database tables
     */
    private void initializeAdminTables() {
        try (Connection conn = dbConnection.getConnection()) {
            // Extend users table with admin fields
            extendUsersTable(conn);
            // Create block_records table
            createBlockRecordsTable(conn);
            // Create admin_logs table
            createAdminLogsTable(conn);
            // Set default admin user
            setDefaultAdmin(conn);
        } catch (SQLException e) {
            System.err.println("Error initializing admin tables: " + e.getMessage());
        }
    }

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
                // Column might already exist, ignore
            }
        }
        System.out.println("Users table extended with admin fields.");
    }

    private void createBlockRecordsTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS block_records (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "admin_id BIGINT NOT NULL, " +
                "block_date TIMESTAMP DEFAULT NOW(), " +
                "unblock_date TIMESTAMP, " +
                "reason TEXT NOT NULL, " +
                "is_active BOOLEAN DEFAULT TRUE, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (admin_id) REFERENCES users(id))";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'block_records' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating block_records table: " + e.getMessage());
        }
    }

    private void createAdminLogsTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS admin_logs (" +
                "id SERIAL PRIMARY KEY, " +
                "admin_id BIGINT NOT NULL, " +
                "action VARCHAR(100) NOT NULL, " +
                "target_user_id BIGINT, " +
                "details TEXT, " +
                "created_at TIMESTAMP DEFAULT NOW(), " +
                "FOREIGN KEY (admin_id) REFERENCES users(id))";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'admin_logs' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating admin_logs table: " + e.getMessage());
        }
    }

    private void setDefaultAdmin(Connection conn) {
        String sql = "UPDATE users SET is_admin = TRUE WHERE username = 'admin' AND is_admin IS NOT TRUE";
        try (Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            if (rows > 0) {
                System.out.println("Default admin user set.");
            }
        } catch (SQLException e) {
            System.err.println("Error setting default admin: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers(String searchQuery, String statusFilter, String sortBy, int page, int pageSize) {
        List<User> users = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT id, username, email, full_name, university, department, " +
            "profile_photo, is_blocked, is_admin, registration_date, last_login " +
            "FROM users WHERE 1=1 "
        );

        // Add search filter
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append("AND (LOWER(username) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) OR LOWER(full_name) LIKE LOWER(?)) ");
        }

        // Add status filter
        if ("Active".equalsIgnoreCase(statusFilter)) {
            sql.append("AND (is_blocked IS NULL OR is_blocked = FALSE) ");
        } else if ("Blocked".equalsIgnoreCase(statusFilter)) {
            sql.append("AND is_blocked = TRUE ");
        }

        // Add sorting
        switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "date":
            case "registration":
                sql.append("ORDER BY registration_date DESC ");
                break;
            case "lastlogin":
            case "last_login":
                sql.append("ORDER BY last_login DESC NULLS LAST ");
                break;
            case "name":
            default:
                sql.append("ORDER BY COALESCE(full_name, username) ASC ");
                break;
        }

        // Add pagination
        sql.append("LIMIT ? OFFSET ?");

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (searchQuery != null && !searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }

            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, page * pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }

        return users;
    }

    @Override
    public int getTotalUsersCount(String searchQuery, String statusFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1 ");

        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append("AND (LOWER(username) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) OR LOWER(full_name) LIKE LOWER(?)) ");
        }

        if ("Active".equalsIgnoreCase(statusFilter)) {
            sql.append("AND (is_blocked IS NULL OR is_blocked = FALSE) ");
        } else if ("Blocked".equalsIgnoreCase(statusFilter)) {
            sql.append("AND is_blocked = TRUE ");
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
            System.err.println("Error getting users count: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean blockUser(Long userId, Long adminId, String reason) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // Update user status
            String updateSql = "UPDATE users SET is_blocked = TRUE WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setLong(1, userId);
                pstmt.executeUpdate();
            }

            // Insert block record
            String insertSql = "INSERT INTO block_records (user_id, admin_id, reason, is_active) VALUES (?, ?, ?, TRUE)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setLong(1, userId);
                pstmt.setLong(2, adminId);
                pstmt.setString(3, reason);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error blocking user: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean unblockUser(Long userId, Long adminId) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // Update user status
            String updateSql = "UPDATE users SET is_blocked = FALSE WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setLong(1, userId);
                pstmt.executeUpdate();
            }

            // Update block record
            String updateBlockSql = "UPDATE block_records SET is_active = FALSE, unblock_date = NOW() " +
                    "WHERE user_id = ? AND is_active = TRUE";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBlockSql)) {
                pstmt.setLong(1, userId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error unblocking user: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean deleteUser(Long userId, Long adminId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public UserActivity getUserActivity(Long userId) {
        UserActivity activity = new UserActivity(userId);

        String sql = "SELECT id, username, email, full_name, profile_photo, is_blocked, " +
                "registration_date, last_login FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    activity.setUsername(rs.getString("username"));
                    activity.setEmail(rs.getString("email"));
                    activity.setFullName(rs.getString("full_name"));
                    activity.setProfilePhoto(rs.getString("profile_photo"));
                    activity.setBlocked(rs.getBoolean("is_blocked"));

                    Timestamp regDate = rs.getTimestamp("registration_date");
                    if (regDate != null) {
                        activity.setRegistrationDate(regDate.toLocalDateTime());
                        activity.setAccountAgeDays((int) ChronoUnit.DAYS.between(
                                regDate.toLocalDateTime(), LocalDateTime.now()));
                    }

                    Timestamp lastLogin = rs.getTimestamp("last_login");
                    if (lastLogin != null) {
                        activity.setLastLogin(lastLogin.toLocalDateTime());
                    }
                }
            }

            // For now, set placeholder values for statistics
            // In a full implementation, these would be calculated from related tables
            activity.setMessagesCount(0);
            activity.setFilesCount(0);
            activity.setGroupsCount(0);
            activity.setSessionsCreated(0);
            activity.setSessionsAttended(0);
            activity.setMostActiveGroup("N/A");
            activity.calculateEngagementScore();

        } catch (SQLException e) {
            System.err.println("Error getting user activity: " + e.getMessage());
        }

        return activity;
    }

    @Override
    public List<BlockRecord> getBlockHistory(Long userId) {
        List<BlockRecord> records = new ArrayList<>();

        String sql = "SELECT br.id, br.user_id, br.admin_id, br.block_date, br.unblock_date, " +
                "br.reason, br.is_active, u.username as admin_username " +
                "FROM block_records br " +
                "LEFT JOIN users u ON br.admin_id = u.id " +
                "WHERE br.user_id = ? " +
                "ORDER BY br.block_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BlockRecord record = new BlockRecord();
                    record.setId(rs.getLong("id"));
                    record.setUserId(rs.getLong("user_id"));
                    record.setAdminId(rs.getLong("admin_id"));
                    record.setAdminUsername(rs.getString("admin_username"));

                    Timestamp blockDate = rs.getTimestamp("block_date");
                    if (blockDate != null) {
                        record.setBlockDate(blockDate.toLocalDateTime());
                    }

                    Timestamp unblockDate = rs.getTimestamp("unblock_date");
                    if (unblockDate != null) {
                        record.setUnblockDate(unblockDate.toLocalDateTime());
                    }

                    record.setReason(rs.getString("reason"));
                    record.setActive(rs.getBoolean("is_active"));

                    records.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting block history: " + e.getMessage());
        }

        return records;
    }

    @Override
    public boolean isUserSoleAdminOfGroups(Long userId) {
        // Placeholder - would check groups table in full implementation
        return false;
    }

    @Override
    public User getUserById(Long userId) {
        String sql = "SELECT id, username, email, full_name, university, department, " +
                "profile_photo, is_blocked, is_admin, registration_date, last_login " +
                "FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void updateLastLogin(Long userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    @Override
    public boolean isAdmin(Long userId) {
        String sql = "SELECT is_admin FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_admin");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin status: " + e.getMessage());
        }

        return false;
    }

    @Override
    public void logAdminAction(Long adminId, String action, Long targetUserId, String details) {
        String sql = "INSERT INTO admin_logs (admin_id, action, target_user_id, details) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, adminId);
            pstmt.setString(2, action);
            if (targetUserId != null) {
                pstmt.setLong(3, targetUserId);
            } else {
                pstmt.setNull(3, Types.BIGINT);
            }
            pstmt.setString(4, details);
            pstmt.executeUpdate();

            System.out.println("Admin action logged: " + action);
        } catch (SQLException e) {
            System.err.println("Error logging admin action: " + e.getMessage());
        }
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setUniversity(rs.getString("university"));
        user.setDepartment(rs.getString("department"));
        user.setProfilePhoto(rs.getString("profile_photo"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setAdmin(rs.getBoolean("is_admin"));

        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            user.setRegistrationDate(regDate.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        return user;
    }
}

