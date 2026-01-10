package com.syncstudy.BL.ChatManager;
import java.time.LocalDateTime;

public class Message {
    private Long id;
    private Long senderId;
    private Long groupId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean isEdited;

    // Sender information for display
    private String senderUsername;
    private String senderFullName;
    private String senderProfilePicture;

    public Message() {}

    public Message(Long senderId, Long groupId, String content) {
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.isEdited = false;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public boolean isEdited() { return isEdited; }
    public void setEdited(boolean edited) { isEdited = edited; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getSenderFullName() { return senderFullName; }
    public void setSenderFullName(String senderFullName) { this.senderFullName = senderFullName; }

    public String getSenderProfilePicture() { return senderProfilePicture; }
    public void setSenderProfilePicture(String senderProfilePicture) { this.senderProfilePicture = senderProfilePicture; }
}

