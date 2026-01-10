package com.syncstudy.PL.ChatManager;

import com.syncstudy.BL.ChatManager.MessageDAO;
import com.syncstudy.BL.ChatManager.Message;
import com.syncstudy.PL.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOPostgres extends MessageDAO {

    private DatabaseConnection dbConnection;

    public MessageDAOPostgres() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            createTableMessages(conn);
        } catch (SQLException e) {
            System.err.println("Error initializing messages table: " + e.getMessage());
        }
    }

    private void createTableMessages(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS messages (" +
                "id SERIAL PRIMARY KEY, " +
                "sender_id BIGINT NOT NULL REFERENCES users(id), " +
                "group_id BIGINT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "modified_at TIMESTAMP, " +
                "is_edited BOOLEAN DEFAULT FALSE)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'messages' created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating messages table: " + e.getMessage());
        }
    }

    @Override
    public Message findById(Long messageId) {
        String sql = "SELECT m.id, m.sender_id, m.group_id, m.content, " +
                "m.created_at, m.modified_at, m.is_edited, " +
                // Alias username as full_name and provide NULL for profile_picture since users table doesn't have these columns
                "u.username AS username, u.username AS full_name, NULL AS profile_picture " +
                "FROM messages m " +
                "JOIN users u ON m.sender_id = u.id " +
                "WHERE m.id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, messageId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMessageFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding message: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Message> findByGroupId(Long groupId) {
        String sql = "SELECT m.id, m.sender_id, m.group_id, m.content, " +
                "m.created_at, m.modified_at, m.is_edited, " +
                // Alias username as full_name and provide NULL for profile_picture
                "u.username AS username, u.username AS full_name, NULL AS profile_picture " +
                "FROM messages m " +
                "JOIN users u ON m.sender_id = u.id " +
                "WHERE m.group_id = ? " +
                "ORDER BY m.created_at ASC";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, groupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(extractMessageFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching messages: " + e.getMessage());
        }

        return messages;
    }

    @Override
    public List<Message> findByGroupIdWithPagination(Long groupId, LocalDateTime beforeTimestamp, int limit) {
        String sql = "SELECT m.id, m.sender_id, m.group_id, m.content, " +
                "m.created_at, m.modified_at, m.is_edited, " +
                // Alias username as full_name and provide NULL for profile_picture
                "u.username AS username, u.username AS full_name, NULL AS profile_picture " +
                "FROM messages m " +
                "JOIN users u ON m.sender_id = u.id " +
                "WHERE m.group_id = ? AND m.created_at < ? " +
                "ORDER BY m.created_at DESC " +
                "LIMIT ?";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, groupId);
            pstmt.setTimestamp(2, Timestamp.valueOf(beforeTimestamp));
            pstmt.setInt(3, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(extractMessageFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching paginated messages: " + e.getMessage());
        }

        return messages;
    }

    @Override
    public Message insert(Message message) {
        String sql = "INSERT INTO messages (sender_id, group_id, content, created_at, is_edited) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, message.getSenderId());
            pstmt.setLong(2, message.getGroupId());
            pstmt.setString(3, message.getContent());
            pstmt.setTimestamp(4, Timestamp.valueOf(message.getCreatedAt()));
            pstmt.setBoolean(5, message.isEdited());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    message.setId(rs.getLong("id"));
                    return message;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting message: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean update(Message message) {
        String sql = "UPDATE messages SET content = ?, modified_at = ?, is_edited = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, message.getContent());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setBoolean(3, true);
            pstmt.setLong(4, message.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating message: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(Long messageId) {
        String sql = "DELETE FROM messages WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, messageId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting message: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean canEditMessage(Long messageId, Long userId) {
        String sql = "SELECT sender_id FROM messages WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, messageId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long senderId = rs.getLong("sender_id");
                    return senderId.equals(userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking edit permission: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean canDeleteMessage(Long messageId, Long userId, boolean isAdmin) {
        if (isAdmin) {
            return true;
        }

        return canEditMessage(messageId, userId);
    }

    private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));

        Object senderObj = rs.getObject("sender_id");
        if (senderObj != null) {
            message.setSenderId(((Number) senderObj).longValue());
        } else {
            message.setSenderId(null);
        }

        Object groupObj = rs.getObject("group_id");
        if (groupObj != null) {
            message.setGroupId(((Number) groupObj).longValue());
        } else {
            message.setGroupId(null);
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            message.setCreatedAt(createdTs.toLocalDateTime());
        }

        Timestamp modifiedAt = rs.getTimestamp("modified_at");
        if (modifiedAt != null) {
            message.setModifiedAt(modifiedAt.toLocalDateTime());
        }

        message.setContent(rs.getString("content"));
        message.setEdited(rs.getBoolean("is_edited"));
        message.setSenderUsername(rs.getString("username"));
        message.setSenderFullName(rs.getString("full_name"));
        message.setSenderProfilePicture(rs.getString("profile_picture"));

        return message;
    }
}
