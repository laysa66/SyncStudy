package com.syncstudy.UI.ProfileManager;

import com.syncstudy.BL.AdminManager.AdminFacade;
import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserDashboardController {
    @FXML private Button profileButton;
    @FXML private Button userInfoButton;
    @FXML private Button logoutButton;
    @FXML private Button submitUpdateButton;
    @FXML private Button deleteAccountButton;
    //change buttons
    @FXML private Label welcomeLabel;
    @FXML private VBox userInfo;

    private SessionFacade session;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        this.session = SessionFacade.getInstance();

        // Set welcome message
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, "+this.session.getCurrentUser().getFullName());
        }

        // Load something by default, but what ??
    }

    public void showUserInfo(User user) {

    }

    public void showProfile(UserProfile profile) {

    }

    public void showOtherProfiles(List<UserProfile> profiles) {

    }

    public void showErrorMessage(String msg) {

    }

    public void handleCreateAccount() {
        this.session.createAccount();
        //go find the credentials inside the window with javafx stuff
    }

    public void handleLogout() {
        this.session.logout();
    }

    public void handleUpdateProfile() {

    }

    public void handleViewOwnProfile() {

    }

    public void handleViewOtherProfiles() {

    }

    public void handleDeleteAccount() {

    }
}
