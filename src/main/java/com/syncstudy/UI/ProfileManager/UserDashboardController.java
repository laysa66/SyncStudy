package com.syncstudy.UI.ProfileManager;

import com.syncstudy.BL.AdminManager.AdminFacade;
import com.syncstudy.BL.ProfileManager.UserProfile;
import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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

    public void handleLogout() {
        if(this.session.logout()) {
            //navigate to next screen when logout
        }
        else {
            //show error
        }
    }

    public void handleUpdateProfile() {
        //go find the credentials inside the window with javafx stuff
        String firstname = "";
        String lastname = "";
        if (this.session.updateProfile(firstname, lastname)) {
            //print on UI profile updated
        }
        else {
            //show error
        }
    }

    public void handleViewOwnProfile() {
        UserProfile profile = this.session.findProfile();
        //show profile
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
}
