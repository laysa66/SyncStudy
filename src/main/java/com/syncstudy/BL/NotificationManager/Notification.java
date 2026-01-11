package com.syncstudy.BL.NotificationManager;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private Long recipientUserId;
    private String type;
    private String content;
    private Long relatedEntityId;
    private LocalDateTime timestamp;
    private boolean readStatus;
    private int notificationAgeDays;

    public Notification() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(Long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public int getNotificationAgeDays() {
        return notificationAgeDays;
    }

    public void setNotificationAgeDays(int notificationAgeDays) {
        this.notificationAgeDays = notificationAgeDays;
    }
}
