package com.syncstudy.BL.ChatManager;

import java.time.LocalDateTime;
import java.util.List;

public abstract class MessageDAO {
    public abstract Message findById(Long messageId);

    public abstract List<Message> findByGroupId(Long groupId);

    public abstract List<Message> findByGroupIdWithPagination(Long groupId, LocalDateTime beforeTimestamp, int limit);

    public abstract Message insert(Message message);

    public abstract boolean update(Message message);

    public abstract boolean delete(Long messageId);

    public abstract boolean canEditMessage(Long messageId, Long userId);

    public abstract boolean canDeleteMessage(Long messageId, Long userId, boolean isAdmin);

}
