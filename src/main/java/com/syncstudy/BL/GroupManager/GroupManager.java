package com.syncstudy.BL.GroupManager;

import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.PL.PostgresFactory;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * GroupManager - Singleton class for managing group operations
 */
public class GroupManager {
    // Singleton instance
    private static GroupManager instance;
    
    // Attributes
    private GroupDAO groupDAO;
    
    // Private constructor for singleton
    private GroupManager() {
        // Initialize groupDAO using factory
        AbstractFactory factory = new PostgresFactory();
        this.groupDAO = factory.createGroupDAO();
    }
    
    // Singleton getInstance method
    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }
        return instance;
    }
    
    // Business methods - exactly as specified in diagram
    public List<Group> search(List<String> searchTerms) {
        return groupDAO.searchGroups(searchTerms);
    }
    
    public List<Group> filter(Optional<Integer> memberCountFilter, 
                            Optional<Category> categoryFilter, 
                            Optional<LocalDateTime> activityFilter) {
        return groupDAO.filterGroups(memberCountFilter, categoryFilter, activityFilter);
    }
    
    public Group findGroupById(Long id) {
        return groupDAO.findGroupById(id);
    }
}
