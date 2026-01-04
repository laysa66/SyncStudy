package com.syncstudy.UI.GroupManager;

/**
 * GroupFrame - Boundary class that uses GroupController (from diagram)
 * This represents the main interface for group management
 */
public class GroupFrame {
    // Attribute from diagram
    private GroupController controller;
    
    /**
     * Sets the controller for this frame
     * @param controller The GroupController instance
     */
    public void setController(GroupController controller) {
        this.controller = controller;
    }
    
    /**
     * Gets the controller for this frame
     * @return The GroupController instance
     */
    public GroupController getController() {
        return controller;
    }
}
