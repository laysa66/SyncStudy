package com.syncstudy.UI.SessionManager;

import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.UI.ChatManager.ChatController;
import com.syncstudy.PL.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class AppUI extends Application {
    private Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database tables at startup
        DatabaseInitializer.initialize();

        this.primaryStage = stage;

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

    private void showChatPage() {
        try {
            URL fxml = AppUI.class.getResource("/com/syncstudy/UI/chat.fxml");
            if (fxml == null) {
                throw new IllegalStateException("FXML resource not found: /com/syncstudy/UI/chat.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();

            ChatController chatController = loader.getController();

            // Get logged-in user from SessionFacade
            SessionFacade sessionFacade = SessionFacade.getInstance();
            User currentUser = sessionFacade.getCurrentUser();

            chatController.setCurrentUser(currentUser.getId(), currentUser.isAdmin());

            // Set a default group (you'll need to modify this based on your requirements)
            // For now, using group ID 1 as an example
            chatController.setCurrentGroup(1L);

            primaryStage.setTitle("SyncStudy - Chat");
            primaryStage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
