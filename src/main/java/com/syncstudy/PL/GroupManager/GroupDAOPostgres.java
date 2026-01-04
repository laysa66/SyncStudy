package com.syncstudy.PL.GroupManager;

import com.syncstudy.BL.GroupManager.GroupDAO;
import com.syncstudy.BL.GroupManager.Group;
import com.syncstudy.BL.GroupManager.Category;
import com.syncstudy.PL.DatabaseConnection;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * GroupDAOPostgres - PostgreSQL implementation of GroupDAO
 */
public class GroupDAOPostgres extends GroupDAO {
    // Attributes
    private DatabaseConnection dbConnection;
    
    // Constructor
    public GroupDAOPostgres() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeDatabase();
    }
    
    // Private initialization method
    private void initializeDatabase() {
        try (Connection conn = dbConnection.getConnection()) {
            createTableGroups(conn);
            createTableCategories(conn);
            insertDefaultCategories(conn);
        } catch (SQLException e) {
            System.err.println("Error initializing Group database: " + e.getMessage());
        }
    }
    
    // Database initialization methods
    public void createTableGroups(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS categories (
                category_id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE,
                description TEXT,
                icon VARCHAR(50),
                color VARCHAR(20)
            );
            
            CREATE TABLE IF NOT EXISTS groups (
                group_id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                creator_id BIGINT NOT NULL,
                category_id BIGINT REFERENCES categories(category_id),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                member_count INTEGER DEFAULT 1,
                icon VARCHAR(50) DEFAULT '👥'
            );
            """;
            
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Groups and Categories tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating groups tables: " + e.getMessage());
        }
    }
    
    private void createTableCategories(Connection conn) {
        // Categories table is created in createTableGroups method
    }
    
    private void insertDefaultCategories(Connection conn) {
        String sql = """
            INSERT INTO categories (category_id, name, description, icon, color) VALUES
            (1, 'Études', 'Groupes d''étude généralistes', '📚', '#007bff'),
            (2, 'Mathématiques', 'Groupes d''étude en mathématiques', '🧮', '#28a745'),
            (3, 'Sciences', 'Groupes d''étude en sciences', '🔬', '#17a2b8'),
            (4, 'Langues', 'Groupes d''étude en langues étrangères', '🌍', '#ffc107'),
            (5, 'Informatique', 'Groupes d''étude en informatique', '💻', '#6f42c1'),
            (6, 'Littérature', 'Groupes d''étude en littérature', '📖', '#e83e8c'),
            (7, 'Histoire', 'Groupes d''étude en histoire', '🏛️', '#fd7e14'),
            (8, 'Préparation aux examens', 'Groupes de préparation aux examens', '🎯', '#dc3545'),
            (9, 'Projet étudiant', 'Groupes de travail sur des projets', '🚀', '#20c997'),
            (10, 'Autre', 'Autres types de groupes d''étude', '📝', '#6c757d')
            ON CONFLICT (category_id) DO NOTHING;
            """;
            
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Default categories inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting default categories: " + e.getMessage());
        }
    }
    
    public void insertGroup(Connection conn, String name, String description, String icon) {
        String sql = "INSERT INTO groups (name, description, icon, creator_id) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, icon != null ? icon : "👥");
            pstmt.setLong(4, 1L); // Default creator_id for initialization
            
            pstmt.executeUpdate();
            System.out.println("Group '" + name + "' inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error inserting group: " + e.getMessage());
        }
    }
    
    // Implementation of abstract methods from GroupDAO
    @Override
    public Group findGroupById(Long id) {
        String sql = """
            SELECT g.*, c.category_id, c.name as category_name, c.description as category_desc, 
                   c.icon as category_icon, c.color as category_color
            FROM groups g 
            LEFT JOIN categories c ON g.category_id = c.category_id 
            WHERE g.group_id = ?
            """;
            
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGroup(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding group by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    @Override
    public List<Group> filterGroups(Optional<Integer> memberCountFilter, 
                                   Optional<Category> categoryFilter, 
                                   Optional<LocalDateTime> activityFilter) {
        StringBuilder sql = new StringBuilder("""
            SELECT g.*, c.category_id, c.name as category_name, c.description as category_desc, 
                   c.icon as category_icon, c.color as category_color
            FROM groups g 
            LEFT JOIN categories c ON g.category_id = c.category_id 
            WHERE 1=1
            """);
            
        List<Object> parameters = new ArrayList<>();
        
        if (memberCountFilter.isPresent()) {
            sql.append(" AND g.member_count >= ?");
            parameters.add(memberCountFilter.get());
        }
        
        if (categoryFilter.isPresent()) {
            sql.append(" AND g.category_id = ?");
            parameters.add(categoryFilter.get().getCategoryId());
        }
        
        if (activityFilter.isPresent()) {
            sql.append(" AND g.last_activity >= ?");
            parameters.add(Timestamp.valueOf(activityFilter.get()));
        }
        
        sql.append(" ORDER BY g.last_activity DESC");
        
        return executeGroupQuery(sql.toString(), parameters);
    }
    
    @Override
    public List<Group> searchGroups(List<String> searchTerms) {
        if (searchTerms == null || searchTerms.isEmpty()) {
            return new ArrayList<>();
        }
        
        StringBuilder sql = new StringBuilder("""
            SELECT g.*, c.category_id, c.name as category_name, c.description as category_desc, 
                   c.icon as category_icon, c.color as category_color
            FROM groups g 
            LEFT JOIN categories c ON g.category_id = c.category_id 
            WHERE 
            """);
            
        List<Object> parameters = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        
        for (String term : searchTerms) {
            conditions.add("(LOWER(g.name) LIKE ? OR LOWER(g.description) LIKE ?)");
            String likePattern = "%" + term.toLowerCase() + "%";
            parameters.add(likePattern);
            parameters.add(likePattern);
        }
        
        sql.append(String.join(" OR ", conditions));
        sql.append(" ORDER BY g.last_activity DESC");
        
        return executeGroupQuery(sql.toString(), parameters);
    }
    
    // Helper methods
    private List<Group> executeGroupQuery(String sql, List<Object> parameters) {
        List<Group> groups = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                groups.add(mapResultSetToGroup(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error executing group query: " + e.getMessage());
        }
        
        return groups;
    }
    
    private Group mapResultSetToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        
        group.setGroupId(rs.getLong("group_id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setCreatorId(rs.getLong("creator_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            group.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastActivity = rs.getTimestamp("last_activity");
        if (lastActivity != null) {
            group.setLastActivity(lastActivity.toLocalDateTime());
        }
        
        group.setMemberCount(rs.getInt("member_count"));
        group.setIcon(rs.getString("icon"));
        
        // Map category if present
        Long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            Category category = new Category();
            category.setCategoryId(categoryId);
            category.setName(rs.getString("category_name"));
            category.setDescription(rs.getString("category_desc"));
            category.setIcon(rs.getString("category_icon"));
            category.setColor(rs.getString("category_color"));
            group.setCategory(category);
        }
        
        return group;
    }
}
