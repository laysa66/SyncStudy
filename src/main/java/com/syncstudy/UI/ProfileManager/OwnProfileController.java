package com.syncstudy.UI.ProfileManager;

import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OwnProfileController {
    // From diagram: uses GroupController
    private UserDashboardController controller;

    @FXML private Label fullnameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label universityLabel;
    @FXML private Label departmentLabel;
    @FXML private Label groupsCountLabel;
    @FXML private Button closeButton;

    private User currentUser;

    /**
     * Initialize the details view
     */
    @FXML
    public void initialize() {

    }

    // Method to set the main controller
    public void setController(UserDashboardController controller) {
        this.controller = controller;
    }

    public void setUser(User user) {
        this.currentUser = user;
        displayUserInfo();
    }

    private void displayUserInfo() {
        if (currentUser == null) return;

        // Simple display logic
        usernameLabel.setText(currentUser.getUsername());
        fullnameLabel.setText(currentUser.getFullName());
        emailLabel.setText(currentUser.getEmail());
        universityLabel.setText(currentUser.getUniversity());
        departmentLabel.setText(currentUser.getDepartment());
        groupsCountLabel.setText("Is in "+currentUser.getGroupsCount()+" groups");

    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
