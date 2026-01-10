package com.syncstudy.BL.GroupManager;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * GroupFacade - Singleton facade for group operations
 */
public class GroupFacade {
    // Singleton instance
    private static GroupFacade instance;
    
    // Attributes
    private GroupManager groupManager;
    
    // Private constructor for singleton
    private GroupFacade() {
        this.groupManager = GroupManager.getInstance();
    }
    
    // Singleton getInstance method
    public static GroupFacade getInstance() {
        if (instance == null) {
            instance = new GroupFacade();
        }
        return instance;
    }
    
    // Facade methods that delegate to GroupManager
    public List<Group> search(List<String> searchTerms) {
        return groupManager.search(searchTerms);
    }
    
    public List<Group> filter(Optional<Integer> memberCountFilter, 
                            Optional<Category> categoryFilter, 
                            Optional<LocalDateTime> activityFilter) {
        return groupManager.filter(memberCountFilter, categoryFilter, activityFilter);
    }
    
    public Group findGroupById(Long id) {
        return groupManager.findGroupById(id);
    }
}
