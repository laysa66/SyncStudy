package com.syncstudy.UI.GroupManager;

import com.syncstudy.BL.GroupManager.Group;
import com.syncstudy.BL.GroupManager.Category;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * GroupDetailsFrame - Boundary class that uses GroupController (from diagram)
 */
public class GroupDetailsFrame {
    // From diagram: uses GroupController
    private GroupController controller;
    
    @FXML private Label groupNameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label memberCountLabel;
    @FXML private Label createdAtLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button closeButton;
    
    private Group currentGroup;

    /**
     * Initialize the details view
     */
    @FXML
    public void initialize() {
        // Simple initialization
        if (descriptionArea != null) {
            descriptionArea.setEditable(false);
        }
    }
    
    // Method to set the main controller (respecting diagram relationship)
    public void setGroupController(GroupController controller) {
        this.controller = controller;
    }
    
    public void setGroup(Group group) {
        this.currentGroup = group;
        displayGroupInfo();
    }
    
    private void displayGroupInfo() {
        if (currentGroup == null) return;
        
        // Simple display logic
        groupNameLabel.setText(currentGroup.getName());
        
        Category cat = currentGroup.getCategory();
        if (cat != null) {
            categoryLabel.setText("Catégorie: " + cat.getName());
        } else {
            categoryLabel.setText("Catégorie: Aucune");
        }
        
        memberCountLabel.setText("Membres: " + currentGroup.getMemberCount());
        
        if (currentGroup.getCreatedAt() != null) {
            String date = currentGroup.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            createdAtLabel.setText("Créé le: " + date);
        }
        
        String desc = currentGroup.getDescription();
        if (desc != null && !desc.trim().isEmpty()) {
            descriptionArea.setText(desc);
        } else {
            descriptionArea.setText("Aucune description disponible.");
        }
    }
    
    @FXML
    private void onClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
