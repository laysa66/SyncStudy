// java
package com.syncstudy.UI.SessionManager;

import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            // proceed to next screen
        } else {
            messageLabel.setText("Invalid username or password");
        }

    }

    private void setMessage(String msg) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
        }
    }
}
