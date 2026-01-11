package com.syncstudy.BL.NotificationManager;

import com.syncstudy.BL.ProfileManager.UserProfile;

import java.util.List;

public abstract class NotificationDAO {
    public abstract void createNotification(Long recipientUserId, String type, String content, Long relatedEntityId);
    public abstract boolean updateNotification(Long notifId, boolean readStatus);
    public abstract Notification findNotificationById(Long notifId);
    public abstract List<Notification> findUserNotifications(Long userId, String searchQuery, String sortBy, String statusFilter, int page, int pageSize);
    public abstract boolean deleteNotification(Long notifId);
    public abstract int getMatchingNotificationCount(String searchQuery, String statusFilter);
}
