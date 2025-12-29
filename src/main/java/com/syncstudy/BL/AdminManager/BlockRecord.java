package com.syncstudy.BL.AdminManager;

import java.time.LocalDateTime;

/**
 * Entity representing a block record for user account management
 * Tracks when users are blocked/unblocked and by whom
 */
public class BlockRecord {
    private Long id;
    private Long userId;
    private Long adminId;
    private String adminUsername;
    private LocalDateTime blockDate;
    private LocalDateTime unblockDate;
    private String reason;
    private boolean isActive;

    public BlockRecord() {
    }

    public BlockRecord(Long userId, Long adminId, String reason) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.blockDate = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public LocalDateTime getBlockDate() {
        return blockDate;
    }

    public void setBlockDate(LocalDateTime blockDate) {
        this.blockDate = blockDate;
    }

    public LocalDateTime getUnblockDate() {
        return unblockDate;
    }

    public void setUnblockDate(LocalDateTime unblockDate) {
        this.unblockDate = unblockDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "BlockRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", blockDate=" + blockDate +
                ", reason='" + reason + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

