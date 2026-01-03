// java
package com.syncstudy.UI.SessionManager;

import com.syncstudy.BL.AdminManager.AdminFacade;
import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.BL.SessionManager.UserManager;
import com.syncstudy.UI.AdminManager.AdminDashboardController;
import com.syncstudy.UI.ProfileManager.RegisterController;
import com.syncstudy.UI.ProfileManager.UserDashboardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private SessionFacade userManager;


    // injected by AppUI after FXMLLoader.load()
    public void setUserManager(SessionFacade userManager) {
        this.userManager = userManager;
    }

    @FXML
    private void onLogin() {
        if (userManager == null) {
            messageLabel.setText("Internal error: UserManager not available.");
            return;
        }
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean ok = userManager.login(username, password);
        if (ok) {
            messageLabel.setText("Login successful");

            // Get user and check if admin
            User user = UserManager.getInstance().findUserByUsername(username);
            if (user != null && user.isAdmin()) {
                navigateToAdminDashboard(user);
            } else {
                messageLabel.setText("Login successful! (Non-admin user)");
                if (user != null) {
                    navigateToUserDashboard(user);
                }
                else {
                    messageLabel.setText("Invalid username or password");
                }
            }
        } else {
            messageLabel.setText("Invalid username or password");
        }
    }

    /**
     * Navigate to admin dashboard
     */
    private void navigateToAdminDashboard(User admin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/syncstudy/UI/AdminManager/AdminDashboard.fxml"));
            Parent dashboard = loader.load();

            // Set admin ID in facade
            AdminDashboardController controller = loader.getController();
            controller.setCurrentAdminId(admin.getId());

            // Switch scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboard));
            stage.setTitle("SyncStudy - Admin Dashboard");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.centerOnScreen();

        } catch (IOException e) {
            messageLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to user dashboard
     */
    private void navigateToUserDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/syncstudy/UI/ProfileManager/UserDashboardView.fxml"));
            Parent dashboard = loader.load();

            // Set user ID in facade
            UserDashboardController controller = loader.getController();
            controller.setCurrentUserId(user.getId());

            // Switch scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
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
     * Navigate to register page
     */
    private void navigateToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/syncstudy/UI/ProfileManager/register.fxml"));
            Parent dashboard = loader.load();

            // Set user ID in facade
            RegisterController controller = loader.getController();
            controller.setSession(userManager);

            // Switch scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboard));
            stage.setTitle("SyncStudy - Sign up");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.centerOnScreen();

        } catch (IOException e) {
            messageLabel.setText("Error loading register: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMessage(String msg) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
        }
    }
}
