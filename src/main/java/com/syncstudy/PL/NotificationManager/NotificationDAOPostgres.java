package com.syncstudy.PL.NotificationManager;

import com.syncstudy.BL.NotificationManager.Notification;
import com.syncstudy.BL.NotificationManager.NotificationDAO;
import com.syncstudy.PL.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOPostgres extends NotificationDAO {
    private DatabaseConnection dbConnection;

    public NotificationDAOPostgres() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            //dropTable(conn);
            createTableNotification(conn);
            //create a test notif for a test user
            createTestNotif(8L,"message","New message in group",3L);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private void createTestNotif(long userId, String type, String content, long relatedEntityId) {
        try {
            String checkSql = "SELECT id FROM notifications WHERE user_id = ? AND type = ? and content = ? and relatedEntityId = ?";
            try (Connection conn = this.dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setLong(1, userId);
                pstmt.setString(2, type);
                pstmt.setString(3, content);
                pstmt.setLong(2, relatedEntityId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return; // Profile already exists
                    }
                }
            }

            createNotification(userId,type,content,relatedEntityId);
        } catch (SQLException e) {
            System.err.println("Error creating test notification for user " + userId + ": " + e.getMessage());
        }
    }

    private void createTableNotification(Connection conn) {
        // Create base table
        String sql = "CREATE TABLE IF NOT EXISTS notifications (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL, " +
                "type VARCHAR(50) NOT NULL, " +
                "content VARCHAR(500) NOT NULL, " +
                "related_entity_id INTEGER NOT NULL, " +
                "timestamp TIMESTAMP DEFAULT NOW(), " +
                "read_status BOOLEAN DEFAULT false, " +
                "CONSTRAINT fk_user " +
                "FOREIGN KEY (user_id) " +
                "REFERENCES users(id) " +
                "ON DELETE CASCADE)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'notifications' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void createNotification(Long recipientUserId, String type, String content, Long relatedEntityId) {
        String sql = "INSERT INTO notifications (user_id, type, content, related_entity_id) VALUES (?, ?, ?, ?) ";

        try (Connection conn = this.dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, recipientUserId);
            pstmt.setString(2, type);
            pstmt.setString(3, content);
            pstmt.setLong(4, relatedEntityId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Notification of type '" + type + "' inserted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting notification: " + e.getMessage());
        }
    }

    @Override
    public boolean updateNotification(Long notifId, boolean readStatus) {
        String sql = "UPDATE notifications SET read_status=?, WHERE id=?";
        try (Connection conn = this.dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, readStatus);
            pstmt.setLong(2, notifId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Notification '" + notifId + "' updated successfully.");
            }
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Notification findNotificationById(Long notifId) {
        String sql = "SELECT id, user_id, type, content, related_entity_id, timestamp, read_status FROM notifications WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, notifId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getLong("id"));
                    notification.setRecipientUserId(rs.getLong("user_id"));
                    notification.setType(rs.getString("type"));
                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) {
                        notification.setTimestamp(ts.toLocalDateTime());
                        notification.setNotificationAgeDays((int) ChronoUnit.DAYS.between(
                                ts.toLocalDateTime(), LocalDateTime.now()));
                    }
                    notification.setReadStatus(rs.getBoolean("read_status"));
                    return notification;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding notification: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Notification> findUserNotifications(Long userId, String searchQuery, String sortBy, String statusFilter, int page, int pageSize) {
        List<Notification> notifications = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, user_id, type, content, related_entity_id, timestamp, read_status " +
                        "FROM notifications WHERE user_id = ? "
        );

        // Add search filter
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append("AND (LOWER(type) LIKE LOWER(?) OR LOWER(content) LIKE LOWER(?)) ");
        }

        // Add status filter
        if ("Read".equalsIgnoreCase(statusFilter)) {
            sql.append("AND read_status = TRUE ");
        } else if ("Unread".equalsIgnoreCase(statusFilter)) {
            sql.append("AND read_status = FALSE ");
        }

        // Add sorting
        switch (sortBy != null ? sortBy.toLowerCase() : "date") {
            case "type":
                sql.append("ORDER BY type ASC, timestamp DESC ");
                break;
            case "status":
                sql.append("ORDER BY read_status ASC, timestamp DESC ");
                break;
            case "date":
            default:
                sql.append("ORDER BY timestamp DESC ");
                break;
        }

        // Add pagination
        sql.append("LIMIT ? OFFSET ?");

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Set user_id parameter
            pstmt.setLong(paramIndex++, userId);

            // Set search parameters
            if (searchQuery != null && !searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }

            // Set pagination parameters
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, page * pageSize);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getLong("id"));
                    notification.setRecipientUserId(rs.getLong("user_id"));
                    notification.setType(rs.getString("type"));
                    notification.setContent(rs.getString("content"));
                    notification.setRelatedEntityId(rs.getLong("related_entity_id"));

                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) {
                        notification.setTimestamp(ts.toLocalDateTime());
                        notification.setNotificationAgeDays((int) ChronoUnit.DAYS.between(
                                ts.toLocalDateTime(), LocalDateTime.now()));
                    }

                    notification.setReadStatus(rs.getBoolean("read_status"));
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user notifications: " + e.getMessage());
        }

        return notifications;
    }

    @Override
    public boolean deleteNotification(Long notifId) {
        String sql = "DELETE FROM notifications WHERE id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, notifId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getMatchingNotificationCount(String searchQuery, String statusFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM notifications WHERE 1=1 ");

        // Add search filter
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append("AND (LOWER(type) LIKE LOWER(?) OR LOWER(content) LIKE LOWER(?)) ");
        }

        // Add status filter
        if ("Read".equalsIgnoreCase(statusFilter)) {
            sql.append("AND read_status = TRUE ");
        } else if ("Unread".equalsIgnoreCase(statusFilter)) {
            sql.append("AND read_status = FALSE ");
        }

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Set search parameters
            if (searchQuery != null && !searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting notifications: " + e.getMessage());
        }

        return 0;
    }
}
