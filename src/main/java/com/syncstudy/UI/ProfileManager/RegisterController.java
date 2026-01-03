package com.syncstudy.UI.ProfileManager;

import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.UI.SessionManager.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class RegisterController {
    //button
    @FXML private Button createAccountButton;
    //Form fields
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField universityField;
    @FXML private TextField departmentField;
    //label for errors or messages
    @FXML private Label messageLabel;
    //session handler
    private SessionFacade session;

    public void setSession(SessionFacade session) {
        this.session = session;
    }

    public void handleCreateAccount() {
        //go find the credentials inside the window with javafx stuff
        String username = usernameField.getText();
        String passwordhash = BCrypt.hashpw(passwordField.getText(),BCrypt.gensalt());
        String email = emailField.getText();
        String firstname = firstnameField.getText();
        String lastname = lastnameField.getText();
        String university = universityField.getText();
        String department = departmentField.getText();

        if(this.session.createAccount(username, passwordhash, email, firstname, lastname, university, department)) {
            setMessage("Account '"+username+"' successfully created");
        }
        else {
            setMessage("Error : account creation failed");
        }
        navigateToLogin();
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
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(dashboard));
            stage.setTitle("SyncStudy - Login");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.centerOnScreen();

        } catch (IOException e) {
            messageLabel.setText("Error loading login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMessage(String msg) {
        if (messageLabel != null) {
            messageLabel.setText(msg);
        }
    }
}
