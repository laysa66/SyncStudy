package com.syncstudy.UI.NotificationManager;

import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.UI.ProfileManager.UserDashboardController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class NotificationCenterController {
    UserDashboardController dashboardController;

    @FXML private ScrollPane notificationsScrollPane;
    @FXML private VBox notificationsContainer;
    @FXML private VBox emptyPlaceholder;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        loadNotificationCards();
    }

    public void setDashboardController(UserDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void loadNotificationCards() {
        /*
        Use FXMLLoader to load each NotificationCard.fxml for each Notification object
Add the loaded cards as children to notificationsContainer.getChildren()
         */
        notificationsContainer.getChildren().addAll()
    }
}
