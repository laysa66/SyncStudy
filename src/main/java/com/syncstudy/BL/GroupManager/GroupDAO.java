package com.syncstudy.BL.GroupManager;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * GroupDAO - Abstract class for Group data access operations
 */
public abstract class GroupDAO {
    
    // Abstract methods
    public abstract List<Group> filterGroups(Optional<Integer> memberCountFilter, 
                                           Optional<Category> categoryFilter, 
                                           Optional<LocalDateTime> activityFilter);
    
    public abstract List<Group> searchGroups(List<String> searchTerms);
    
    public abstract Group findGroupById(Long id);
}
