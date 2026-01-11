package com.syncstudy.BL.NotificationManager;

import com.syncstudy.BL.ProfileManager.ProfileManager;
import com.syncstudy.BL.SessionManager.SessionFacade;
import com.syncstudy.BL.SessionManager.UserManager;

import java.util.List;

public class NotificationFacade {
    private static NotificationFacade instance;
    private NotificationManager notificationManager;

    private NotificationFacade() {
        this.notificationManager = NotificationManager.getInstance();
    }

    /**
     * Get the singleton instance of NotificationFacade
     * @return NotificationFacade instance
     */
    public static NotificationFacade getInstance() {
        if (instance == null) {
            synchronized (NotificationFacade.class) {
                if (instance == null) {
                    instance = new NotificationFacade();
                }
            }
        }
        return instance;
    }

    public List<Notification> findUserNotifications(Long userId, String search, String sortBy, String statusFilter) {
        return notificationManager.findUserNotifications(userId,search,sortBy,statusFilter);
    }

    public void markAsRead(Long notifId) {
        notificationManager.updateNotificationStatus(notifId,true);
    }

    public void markAsUnread(Long notifId) {
        notificationManager.updateNotificationStatus(notifId,false);
    }

    public void deleteNotification(Long notifId) {
        notificationManager.deleteNotification(notifId);
    }
}
