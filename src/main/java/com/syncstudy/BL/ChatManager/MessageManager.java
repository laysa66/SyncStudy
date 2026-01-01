package com.syncstudy.BL.ChatManager;

import com.syncstudy.PL.ChatManager.MessageDAOPostgres;

import java.time.LocalDateTime;
import java.util.List;

public class MessageManager {

    private MessageDAO messageDAO;

    public MessageManager() {
        this.messageDAO = new MessageDAOPostgres();
    }

    public List<Message> getMessages(Long groupId) {
        return messageDAO.findByGroupId(groupId);
    }

    public List<Message> getMessagesWithPagination(Long groupId, LocalDateTime beforeTimestamp, int limit) {
        return messageDAO.findByGroupIdWithPagination(groupId, beforeTimestamp, limit);
    }

    public Message sendMessage(Long senderId, Long groupId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        Message message = new Message(senderId, groupId, content.trim());
        return messageDAO.insert(message);
    }

    public boolean editMessage(Long messageId, Long userId, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (!messageDAO.canEditMessage(messageId, userId)) {
            throw new SecurityException("You do not have permission to perform this action. You can only edit your own messages.");
        }

        Message message = messageDAO.findById(messageId);
        if (message == null) {
            throw new IllegalStateException("This message has been deleted and is no longer available.");
        }

        message.setContent(newContent.trim());
        message.setModifiedAt(LocalDateTime.now());
        message.setEdited(true);

        return messageDAO.update(message);
    }

    public boolean deleteMessage(Long messageId, Long userId, boolean isAdmin) {
        if (!messageDAO.canDeleteMessage(messageId, userId, isAdmin)) {
            throw new SecurityException("You do not have permission to perform this action. You can only delete your own messages.");
        }

        Message message = messageDAO.findById(messageId);
        if (message == null) {
            throw new IllegalStateException("This message has been deleted and is no longer available.");
        }

        return messageDAO.delete(messageId);
    }

    public Message getMessage(Long messageId) {
        return messageDAO.findById(messageId);
    }
}

