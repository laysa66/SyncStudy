package com.syncstudy.PL.NotificationManager;

import com.syncstudy.BL.NotificationManager.Notification;
import com.syncstudy.BL.NotificationManager.NotificationDAO;
import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.PL.DatabaseConnection;

import java.sql.*;
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
    public boolean updateNotification(boolean readStatus) {
        
    }

    @Override
    public Notification findNotificationById(Long notifId) {
        return null;
    }

    @Override
    public List<Notification> findUserNotifications(Long userId, String searchQuery, String sortBy, String statusFilter, int page, int pageSize) {
        return List.of();
    }

    @Override
    public boolean deleteNotification(Long notifId) {
        return false;
    }

    @Override
    public int getMatchingNotificationCount(String searchQuery) {
        return 0;
    }
}
