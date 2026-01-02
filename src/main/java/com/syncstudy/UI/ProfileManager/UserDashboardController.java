package com.syncstudy.UI.ProfileManager;

import com.syncstudy.BL.AdminManager.AdminFacade;
import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.UI.SessionManager.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserDashboardController {
    @FXML private Button profileButton;
    @FXML private Button userInfoButton;
    @FXML private Button logoutButton;
    @FXML private Button createAccountButton;
    @FXML private Button submitUpdateButton;
    @FXML private Button deleteAccountButton;
    //change buttons
    @FXML private Label welcomeLabel;
    @FXML private Label messageLabel;
    @FXML private VBox userInfo;
    @FXML private BorderPane mainPane;
    @FXML private VBox sidebar;

    private SessionFacade session;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        this.session = SessionFacade.getInstance();

        // Set welcome message
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, User");
            //+this.session.getCurrentUser().getFullName()
        }

        // Load something by default, but what ??
    }

    public void showUserInfo(User user) {

    }

    /**
     * Loads
     * @param profile
     */
    public void showProfile(UserProfile profile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/syncstudy/UI/ProfileManager/OwnProfile.fxml"));
            Parent profileView = loader.load();
            mainPane.setCenter(profileView);

            // Update button states
            updateButtonStyles(profileButton);
        } catch (IOException e) {
            showErrorMessage("Failed to load profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showOtherProfiles(List<UserProfile> profiles) {

    }

    /**
     * Show error message as an alert
     * @param msg the error message to show
     */
    public void showErrorMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void handleCreateAccount() {
        //go find the credentials inside the window with javafx stuff
        String username = "";
        String passwordhash = "";
        String email = "";
        String firstname = "";
        String lastname = "";
        String university = "";
        String department = "";

        if(this.session.createAccount(username, passwordhash, email, firstname, lastname, university, department)) {
            //print on UI account created
        }
        else {
            //show error
        }
    }

    /**
     * Handles a click on the logout button
     */
    public void handleLogout() {
        if(this.session.logout()) {
            navigateToLogin();
        }
        else {
            showErrorMessage("Logout failed");
        }
    }

    public void handleUpdateProfile() {
        //go find the credentials inside the window with javafx stuff
        String firstname = "";
        String lastname = "";
        if (this.session.updateProfile(firstname, lastname)) {
            messageLabel.setText("Profile updated");
        }
        else {
            showErrorMessage("Update failed");
        }
    }

    /**
     * Handles a click on ViewProfile button
     * Loads and shows the logged user's own profile
     */
    public void handleViewOwnProfile() {
        UserProfile profile = this.session.findProfile();
        if (profile != null) {
            showProfile(profile);
        }
        else {
            showErrorMessage("Profile not found");
        }
    }

    public void handleViewOtherProfiles() {
        //go find the search queries inside the window with javafx stuff
        String searchQuery = "";
        String sortBy = "";
        int page = 0;
        int pageSize = 0;
        List<UserProfile> profiles = this.session.findAllProfiles(searchQuery, sortBy, page, pageSize);
        //show list
    }

    public void handleDeleteAccount() {
        //go find the credentials inside the window with javafx stuff
        if (this.session.deleteAccount()) {
            //print on UI account deleted
            //navigate accordingly
        }
        else {
            //show error
        }

    }

    public void setCurrentUserId(Long userId) {
        this.session.setLoggedUserId(userId);
    }

    /**
     * Navigate to login page
     */
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/syncstudy/UI/login.fxml"));
            Parent dashboard = loader.load();

            //setup new controller to handle login page
            LoginController controller = loader.getController();
            controller.setUserManager(session);

            // Switch scene
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(dashboard));
            stage.setTitle("SyncStudy - User Dashboard");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.centerOnScreen();

        } catch (IOException e) {
            messageLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update sidebar button styles
     */
    private void updateButtonStyles(Button activeButton) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";
        String activeStyle = "-fx-background-color: #495057; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";

        // Reset all buttons
        if (profileButton != null) {
            profileButton.setStyle(defaultStyle);
        }

        // Set active button
        if (activeButton != null) {
            activeButton.setStyle(activeStyle);
        }
    }
}
