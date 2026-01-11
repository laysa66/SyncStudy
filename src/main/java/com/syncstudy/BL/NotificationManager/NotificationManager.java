package com.syncstudy.BL.NotificationManager;


import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.PL.PostgresFactory;

import java.util.List;

public class NotificationManager {
    private static NotificationManager instance;
    private NotificationDAO notificationDAO;

    private NotificationManager() {
        // Initialize with concrete factory (can be changed for other DB types)
        AbstractFactory factory = new PostgresFactory();
        this.notificationDAO = factory.createNotificationDAO();
    }

    /**
     * Get the singleton instance of NotificationManager
     * @return NotificationManager instance
     */
    public static NotificationManager getInstance() {
        if (instance == null) {
            synchronized (NotificationManager.class) {
                if (instance == null) {
                    instance = new NotificationManager();
                }
            }
        }
        return instance;
    }

    public void createNotification(Long recipientUserID, String type, String content, Long relatedEntityId) {
        notificationDAO.createNotification(recipientUserID,type,content,relatedEntityId);
    }

    public void deleteNotification(Long notifId) {
        notificationDAO.deleteNotification(notifId);
    }

    public Notification findNotificationById(Long notifId) {
        return notificationDAO.findNotificationById(notifId);
    }

    public int getMatchingNotificationsCount(String searchQuery, String statusFilter) {
        return notificationDAO.getMatchingNotificationCount(searchQuery,statusFilter);
    }

    public List<Notification> findUserNotifications(Long userId, String searchQuery, String sortBy, String statusFilter) {
        return notificationDAO.findUserNotifications(userId,searchQuery,sortBy,statusFilter);
    }



}
