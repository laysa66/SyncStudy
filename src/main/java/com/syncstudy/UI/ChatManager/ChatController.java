package com.syncstudy.UI.ChatManager;
import com.google.gson.Gson;
import com.syncstudy.BL.ChatManager.ChatFacade;
import com.syncstudy.BL.ChatManager.Message;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
 import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextArea messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private Label errorLabel;

    private ChatFacade messageService;
    private Long currentUserId;
    private Long currentGroupId;
    private boolean isAdmin;
    private TcpChatClient tcpClient;



    public void initialize() {
        messageService = ChatFacade.getInstance();
        errorLabel.setVisible(false);

        // Auto-scroll to bottom when new content is added
        messageContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });

        // Setup infinite scroll
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() == 0.0) {
                loadOlderMessages();
            }
        });
    }

    public void setCurrentUser(Long userId, boolean isAdmin) {
        this.currentUserId = userId;
        this.isAdmin = isAdmin;
    }

    public void setCurrentGroup(Long groupId) {
        this.currentGroupId = groupId;
        loadMessages();
    }

    private void loadMessages() {
        Platform.runLater(() -> {
            messageContainer.getChildren().clear();
            List<Message> messages = messageService.getMessages(currentGroupId);

            if (messages.isEmpty()) {
                showEmptyState();
            } else {
                for (Message message : messages) {
                    displayMessage(message);
                }
            }
        });
    }

    private void loadOlderMessages() {
        // Implementation for infinite scroll
        // Get timestamp of oldest message and load more
    }

    @FXML
    private void handleSendMessage() {
        String content = messageInput.getText();

        try {
            Message message = messageService.sendMessage(currentUserId, currentGroupId, content);

            // If a message was created successfully, remove any empty-state placeholder
            if (message != null) {
                // Remove the "No messages yet" empty state if present
                if (!messageContainer.getChildren().isEmpty()) {
                    // Check first child for empty state markers
                    if (messageContainer.getChildren().get(0) instanceof VBox) {
                        VBox firstChild = (VBox) messageContainer.getChildren().get(0);
                        if (!firstChild.getChildren().isEmpty()) {
                            if (firstChild.getChildren().get(0) instanceof Label) {
                                Label possibleEmptyLabel = (Label) firstChild.getChildren().get(0);
                                if ("No messages yet".equals(possibleEmptyLabel.getText())) {
                                    messageContainer.getChildren().clear();
                                }
                            }
                        }
                    }

                }

//                displayMessage(message);
                // send an envelope so server will broadcast to other clients:
                if (tcpClient != null) {
                    TcpChatClient.EventEnvelope envelope = new TcpChatClient.EventEnvelope("new", message, null);
                    tcpClient.sendEvent(envelope);
                }
                messageInput.clear();
                errorLabel.setVisible(false);
            } else {
                showError("Failed to send message.");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void displayMessage(Message message) {
        VBox messageBox = createMessageBox(message);
        messageContainer.getChildren().add(messageBox);
    }

    private VBox createMessageBox(Message message) {
        VBox messageBox = new VBox(5);
        messageBox.setMaxWidth(600);

        if (message == null) {
            // Defensive: if message is null, return an empty placeholder
            Label error = new Label("[invalid message]");
            messageBox.getChildren().add(error);
            return messageBox;
        }

        Long senderIdObj = message.getSenderId();
        boolean isOwnMessage = senderIdObj != null && senderIdObj.equals(currentUserId);

        HBox container = new HBox(10);
        container.setAlignment(isOwnMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        if (!isOwnMessage) {
            Circle avatar = new Circle(16);
            avatar.setFill(Color.LIGHTBLUE);
            container.getChildren().add(avatar);
        }

        VBox bubbleContainer = new VBox(3);

//        if (!isOwnMessage) {
        String senderFullName = message.getSenderFullName();
        String senderUsername = message.getSenderUsername();
        String senderDisplay = (senderFullName != null && !senderFullName.isEmpty()) ? senderFullName : senderUsername;

        Label senderName = new Label(senderDisplay != null ? senderDisplay : "");
        senderName.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        // Align sender name according to message side
        senderName.setAlignment(isOwnMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        bubbleContainer.getChildren().add(senderName);
        //}

        HBox bubble = new HBox();
        bubble.setStyle(isOwnMessage ?
                "-fx-background-color: #E3F2FD; -fx-background-radius: 10; -fx-padding: 10;" :
                "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10; -fx-border-color: #E0E0E0; -fx-border-radius: 10;");

        Text messageText = new Text(message.getContent() != null ? message.getContent() : "");
        messageText.setWrappingWidth(400);
        bubble.getChildren().add(messageText);

        bubbleContainer.getChildren().add(bubble);

        HBox timestampBox = new HBox(5);
        String timeText = message.getCreatedAt() != null ? message.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
        Label timestamp = new Label(timeText);
        timestamp.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
        timestampBox.getChildren().add(timestamp);

        if (message.isEdited()) {
            Label editedLabel = new Label("Edited");
            editedLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray; -fx-font-style: italic;");
            timestampBox.getChildren().add(editedLabel);
        }

        bubbleContainer.getChildren().add(timestampBox);

        // Add context menu for edit/delete
        ContextMenu contextMenu = createContextMenu(message);
        bubble.setOnContextMenuRequested(e -> {
            contextMenu.show(bubble, e.getScreenX(), e.getScreenY());
        });

        container.getChildren().add(bubbleContainer);
        messageBox.getChildren().add(container);

        return messageBox;
    }

    private ContextMenu createContextMenu(Message message) {
        ContextMenu contextMenu = new ContextMenu();

        Long senderIdObj = message.getSenderId();
        boolean isOwner = senderIdObj != null && senderIdObj.equals(currentUserId);

        if (isOwner) {
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> handleEditMessage(message));
            contextMenu.getItems().add(editItem);
        }

        if (isOwner || isAdmin) {
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> handleDeleteMessage(message));
            contextMenu.getItems().add(deleteItem);
        }

        return contextMenu;
    }

    private void handleEditMessage(Message message) {
        TextInputDialog dialog = new TextInputDialog(message.getContent());
        dialog.setTitle("Edit Message");
        dialog.setHeaderText("Edit your message");
        dialog.setContentText("Message:");

        dialog.showAndWait().ifPresent(newContent -> {
            try {
                messageService.editMessage(message.getId(), currentUserId, newContent);
                loadMessages();
                if (tcpClient != null) {
                    TcpChatClient.EventEnvelope envelope = new TcpChatClient.EventEnvelope("edit", null, message.getId());
                    tcpClient.sendEvent(envelope);
                }
                showSuccess("Message updated.");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    private void handleDeleteMessage(Message message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Message");
        alert.setHeaderText("Delete this message?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    messageService.deleteMessage(message.getId(), currentUserId, isAdmin);
                    loadMessages();
                    if (tcpClient != null) {
                        TcpChatClient.EventEnvelope envelope = new TcpChatClient.EventEnvelope("delete", null, message.getId());
                        tcpClient.sendEvent(envelope);
                    }
                    showSuccess("Message deleted.");
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void showEmptyState() {
        VBox emptyState = new VBox(10);
        emptyState.setAlignment(Pos.CENTER);
        Label noMessages = new Label("No messages yet");
        noMessages.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        Label startConversation = new Label("Start the conversation!");
        startConversation.setStyle("-fx-text-fill: gray;");
        emptyState.getChildren().addAll(noMessages, startConversation);
        messageContainer.getChildren().add(emptyState);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        // Show success toast notification
        System.out.println(message);
    }

    public void startRealtime(String host, int port) {
        if (tcpClient != null) return;
        tcpClient = new TcpChatClient(host, port);
        try {
            tcpClient.connect(this);
        } catch (Exception e) {
            showError("Realtime connect failed: " + e.getMessage());
        }
    }

    // Add method to stop realtime (call on app shutdown)
    public void stopRealtime() {
        if (tcpClient != null) {
            tcpClient.disconnect();
            tcpClient = null;
        }
    }
    // This method is called by TcpChatClient on the JavaFX thread
    public void handleRemoteEnvelope(TcpChatClient.EventEnvelope env) {
        if (env == null) return;
        switch (env.type) {
            case "new":
                if (env.message != null) {
                    // avoid duplicates depending on your UI logic; simple append:
                    displayMessage(env.message);
                    loadMessages();
                }
                break;
            case "edit":
                // simplest: reload messages to reflect edits
                loadMessages();
                break;
            case "delete":
                // simplest: reload messages to reflect deletion
                loadMessages();
                break;
        }
    }
}
