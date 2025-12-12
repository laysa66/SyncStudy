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

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText("");
        }
    }

    @FXML
    private void onLogin() {
        String username = usernameField == null || usernameField.getText() == null
                ? "" : usernameField.getText().trim();
        String password = passwordField == null || passwordField.getText() == null
                ? "" : passwordField.getText();

        if (username.isEmpty()) {
            setMessage("Please enter username.");
            return;
        }

        // Use SessionFacade for authentication (wraps UserManager)
        boolean ok = SessionFacade.getInstance().login(username, password);
        if (ok) {
            String display = username;
            try {
                User u = UserManager.getInstance().findUserByUsername(username);
                if (u != null) {
                    // try common getters without assuming specific API
                    try {
                        display = (String) u.getClass().getMethod("getDisplayName").invoke(u);
                    } catch (NoSuchMethodException e1) {
                        try {
                            display = (String) u.getClass().getMethod("getUsername").invoke(u);
                        } catch (NoSuchMethodException e2) {
                            display = u.toString();
                        }
                    }
                    if (display == null || display.trim().isEmpty()) {
                        display = username;
                    }
                }
            } catch (Exception ignored) {
                display = username;
            }
            setMessage("Welcome, " + display + "!");
            // proceed to next view or notify application here
        } else {
            setMessage("Invalid username or password.");
        }
    }

    private void setMessage(String msg) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
        }
    }
}
