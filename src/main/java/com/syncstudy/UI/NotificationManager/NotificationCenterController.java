package com.syncstudy.UI.NotificationManager;

import com.syncstudy.BL.NotificationManager.Notification;
import com.syncstudy.BL.NotificationManager.NotificationFacade;
import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.UI.ProfileManager.UserDashboardController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class NotificationCenterController {
    UserDashboardController dashboardController;
    private SessionFacade session;
    private NotificationFacade notificationFacade;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortByCombo;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ScrollPane notificationsScrollPane;
    @FXML private VBox notificationsContainer;
    @FXML private VBox emptyPlaceholder;
    @FXML private Label totalNotificationsLabel; // FIXED: was totalUsersLabel in loadNotifications()

    private ObservableList<Notification> notificationsList;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        this.session = SessionFacade.getInstance();
        this.notificationFacade = NotificationFacade.getInstance();
        this.notificationsList = FXCollections.observableArrayList();

        setupFilters();
        loadNotificationCards();
    }

    public void setDashboardController(UserDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /**
     * Setup filter controls
     */
    private void setupFilters() {
        // Status filter
        statusFilter.setItems(FXCollections.observableArrayList("All", "Read", "Unread"));
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> loadNotificationCards());

        // Sort by
        sortByCombo.setItems(FXCollections.observableArrayList("Newest First", "Oldest First", "Type"));
        sortByCombo.setValue("Newest First");
        sortByCombo.setOnAction(e -> loadNotificationCards());

        // Search field listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            loadNotificationCards();
        });
    }

    /**
     * Load and display notification cards
     */
    public void loadNotificationCards() {
        String search = searchField.getText();
        String sortBy = getSortByValue();
        String status = getStatusFilter();
        Long userId = this.session.getCurrentUser().getId();

        // Get all notifications from facade
        List<Notification> notifs = notificationFacade.findUserNotifications(userId, search, sortBy, status);

        // Clear existing cards
        notificationsContainer.getChildren().clear();
        
        notificationsList.setAll(notifs);

        // Update display
        if (notifs.isEmpty()) {
            // Show empty placeholder
            emptyPlaceholder.setVisible(true);
            emptyPlaceholder.setManaged(true);
            notificationsContainer.getChildren().add(emptyPlaceholder);
            totalNotificationsLabel.setText("No notifications found");
        } else {
            // Hide empty placeholder
            emptyPlaceholder.setVisible(false);
            emptyPlaceholder.setManaged(false);

            // Load each notification card
            for (Notification notification : notifs) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NotificationCard.fxml"));
                    VBox card = loader.load();

                    // Get the card controller and set the notification data
                    NotificationCardController cardController = loader.getController();
                    cardController.setNotification(notification);
                    cardController.setParentController(this); // So card can refresh parent when marked as read/deleted

                    // Add card to container
                    notificationsContainer.getChildren().add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                    dashboardController.showErrorMessage("Error loading notifications: "+e.getMessage());
                }
            }

            // Update label
            totalNotificationsLabel.setText(String.format("Showing %d notification%s",
                    notifs.size(), notifs.size() == 1 ? "" : "s"));
        }
    }

    /**
     * Handle clear filters button
     */
    @FXML
    private void handleClearFilters() {
        searchField.clear();
        statusFilter.setValue("All");
        sortByCombo.setValue("Newest First");
        loadNotificationCards();
    }

    private String getStatusFilter() {
        return this.statusFilter.getValue();
    }

    private String getSortByValue() {
        return this.sortByCombo.getValue();
    }
}

