package com.syncstudy.BL.GroupManager;

/**
 * Category - Entity class representing a group category
 */
public class Category {
    // Attributes
    private Long categoryId;       // PK
    private String name;
    private String description;
    private String icon;
    private String color;
    
    // Constructors
    public Category() {
    }
    
    public Category(String name, String description, String icon, String color) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
    }
    
    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
}
