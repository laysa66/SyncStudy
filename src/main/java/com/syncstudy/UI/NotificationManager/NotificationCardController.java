package com.syncstudy.UI.NotificationManager;

import com.syncstudy.BL.NotificationManager.Notification;
import com.syncstudy.BL.NotificationManager.NotificationFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.time.Duration;
import java.time.LocalDateTime;

public class NotificationCardController {

    // Reference to parent controller to refresh the list after actions
    private NotificationCenterController parentController;

    // The notification data this card displays
    private Notification notification;

    // Facade for business logic
    private NotificationFacade notificationFacade;

    // FXML elements - these match the fx:id values in NotificationCard.fxml
    @FXML private HBox cardContainer;
    @FXML private CheckBox selectCheckBox;
    @FXML private Circle unreadIndicator;
    @FXML private StackPane notificationIcon;
    @FXML private Label iconSymbol;
    @FXML private Label notificationText;
    @FXML private Label timestampLabel;
    @FXML private Button markReadButton;
    @FXML private Button markUnreadButton;
    @FXML private Button deleteButton;

    /**
     * Initialize the controller (called automatically by JavaFX)
     */
    @FXML
    public void initialize() {
        this.notificationFacade = NotificationFacade.getInstance();
    }

    /**
     * Set the notification data and update the UI
     */
    public void setNotification(Notification notification) {
        this.notification = notification;
        updateUI();
    }

    /**
     * Set the parent controller reference
     */
    public void setParentController(NotificationCenterController parentController) {
        this.parentController = parentController;
    }

    /**
     * Update all UI elements based on the notification data
     */
    private void updateUI() {
        if (notification == null) return;

        // Set notification text
        notificationText.setText(notification.getContent());

        // Set timestamp (convert to "X hours ago" format)
        timestampLabel.setText(formatTimestamp(notification.getTimestamp()));

        // Configure read/unread appearance
        if (notification.isReadStatus()) {
            // READ: white background, normal text, no dot, hide "Mark as Read" button
            cardContainer.getStyleClass().remove("notification-card-unread");
            cardContainer.getStyleClass().add("notification-card");
            notificationText.getStyleClass().remove("notification-content-unread");
            notificationText.getStyleClass().add("notification-content");
            unreadIndicator.setVisible(false);
            unreadIndicator.setManaged(false);
            markReadButton.setVisible(false);
            markReadButton.setManaged(false);
            markUnreadButton.setVisible(true);
            markUnreadButton.setManaged(true);
        } else {
            // UNREAD: blue background, bold text, blue dot, show "Mark as Read" button
            cardContainer.getStyleClass().remove("notification-card");
            cardContainer.getStyleClass().add("notification-card-unread");
            notificationText.getStyleClass().remove("notification-content");
            notificationText.getStyleClass().add("notification-content-unread");
            unreadIndicator.setVisible(true);
            unreadIndicator.setManaged(true);
            markReadButton.setVisible(true);
            markReadButton.setManaged(true);
            markUnreadButton.setVisible(false);
            markUnreadButton.setManaged(false);
        }

        // Set icon based on notification type
        setIconBasedOnType(notification.getType());
    }

    /**
     * Set the icon appearance based on notification type
     */
    private void setIconBasedOnType(String type) {
        // Remove all icon type classes first
        notificationIcon.getStyleClass().removeAll(
                "notification-icon-message",
                "notification-icon-file",
                "notification-icon-calendar",
                "notification-icon-user",
                "notification-icon-alert",
                "notification-icon-default"
        );

        // Add the appropriate class and symbol based on type
        if (type == null) {
            notificationIcon.getStyleClass().add("notification-icon-default");
            iconSymbol.setText("🔔");
            return;
        }

        switch (type.toLowerCase()) {
            case "message":
                notificationIcon.getStyleClass().add("notification-icon-message");
                iconSymbol.setText("💬");
                break;
            case "file":
                notificationIcon.getStyleClass().add("notification-icon-file");
                iconSymbol.setText("📄");
                break;
            case "calendar":
            case "event":
                notificationIcon.getStyleClass().add("notification-icon-calendar");
                iconSymbol.setText("📅");
                break;
            case "user":
            case "profile":
                notificationIcon.getStyleClass().add("notification-icon-user");
                iconSymbol.setText("👤");
                break;
            case "alert":
            case "warning":
                notificationIcon.getStyleClass().add("notification-icon-alert");
                iconSymbol.setText("⚠️");
                break;
            default:
                notificationIcon.getStyleClass().add("notification-icon-default");
                iconSymbol.setText("🔔");
                break;
        }
    }

    /**
     * Format timestamp to "X hours ago" format
     */
    private String formatTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) return "Unknown time";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }

    /**
     * Handle "Mark as Read" button click
     */
    @FXML
    private void handleMarkAsRead() {
        if (notification == null) return;

        // Update notification status in backend
        notificationFacade.markAsRead(notification.getId());

        // Update local object
        notification.setReadStatus(true);

        // Refresh UI
        updateUI();

        // Optionally refresh parent list to update counts
        if (parentController != null) {
            parentController.loadNotificationCards();
        }
    }

    /**
     * Handle "Mark as Unread" button click
     */
    @FXML
    private void handleMarkAsUnread() {
        if (notification == null) return;

        // Update notification status in backend
        notificationFacade.markAsUnread(notification.getId());

        // Update local object
        notification.setReadStatus(false);

        // Refresh UI
        updateUI();

        // Optionally refresh parent list to update counts
        if (parentController != null) {
            parentController.loadNotificationCards();
        }
    }

    /**
     * Handle "Delete" button click
     */
    @FXML
    private void handleDelete() {
        if (notification == null) return;

        // Delete notification in backend
        notificationFacade.deleteNotification(notification.getId());

        // Refresh parent list to remove this card
        if (parentController != null) {
            parentController.loadNotificationCards();
        }
    }

    /**
     * Get the checkbox (used by parent for bulk operations)
     */
    public CheckBox getSelectCheckBox() {
        return selectCheckBox;
    }

    /**
     * Get the notification (used by parent for bulk operations)
     */
    public Notification getNotification() {
        return notification;
    }
}
