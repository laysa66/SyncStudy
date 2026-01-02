package com.syncstudy.UI.SessionManager;

import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.UI.ChatManager.ChatController;
import com.syncstudy.PL.DatabaseInitializer;
import com.syncstudy.WS.TcpChatServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.BindException;
import java.net.URL;

public class AppUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database tables at startup
        DatabaseInitializer.initialize();

        // Start embedded TCP server in background (daemon) so it doesn't block JavaFX thread.
        startEmbeddedServer(9000);


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

    private void startEmbeddedServer(int port) {
        Thread serverThread = new Thread(() -> {
            try {
                new TcpChatServer(port).start();
            } catch (BindException be) {
                // Port already in use — likely a separate server instance is running; proceed without failing.
                System.out.println("Embedded server: port " + port + " already in use; assuming external server is running.");
            } catch (IOException e) {
                System.err.println("Embedded server failed to start: " + e.getMessage());
            }
        }, "Embedded-TcpChatServer");
        serverThread.setDaemon(true); // allow JVM to exit when only this thread remains
        serverThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
