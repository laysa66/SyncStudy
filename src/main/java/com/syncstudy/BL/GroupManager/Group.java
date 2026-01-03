package com.syncstudy.BL.GroupManager;

import java.time.LocalDateTime;

/**
 * Group - Entity class representing a study group
 */
public class Group {
    // Attributes
    private Long groupId;          // PK
    private String name;
    private String description;
    private Long creatorId;        // FK
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private int memberCount;
    private String icon;
    private Category category;     
    
    // Constructors
    public Group() {
    }
    
    public Group(String name, String description, Long creatorId) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.memberCount = 1; 
    }
    
    // Getters and Setters
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public int getMemberCount() {
        return memberCount;
    }
    
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
}
