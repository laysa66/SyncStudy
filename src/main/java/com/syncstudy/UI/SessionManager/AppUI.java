package com.syncstudy.UI.SessionManager;

import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.UserManager;
import com.syncstudy.PL.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class AppUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database tables at startup
        DatabaseInitializer.initialize();

        URL fxml = AppUI.class.getResource("/com/syncstudy/UI/login.fxml");
        if (fxml == null) {
            throw new IllegalStateException("FXML resource not found: /com/syncstudy/UI/login.fxml. "
                    + "Make sure the file exists under src/main/resources/com/syncstudy/UI/");
        }

        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof LoginController) {
            ((LoginController) controller).setUserManager(SessionFacade.getInstance());
        }

        stage.setTitle("SyncStudy - Login");
        stage.setScene(new Scene(root, 480, 320));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
