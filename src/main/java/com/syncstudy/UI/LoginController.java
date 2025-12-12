// java
package com.syncstudy.UI;

import com.syncstudy.BL.SessionFacade;
import com.syncstudy.BL.User;
import com.syncstudy.BL.UserManager;
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

    private UserManager userManager;


    // injected by AppUI after FXMLLoader.load()
    public void setUserManager(UserManager userManager) {
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

        boolean ok = userManager.checkCredentials(username, password);
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
