package com.syncstudy.BL.AdminManager;

import com.syncstudy.BL.SessionManager.User;
import java.util.List;

/**
 * Abstract Data Access Object for Admin operations
 * Defines the contract for admin persistence operations
 */
public abstract class AdminDAO {

    /**
     * Get all users with optional filtering and sorting
     * @param searchQuery search term for name or email
     * @param statusFilter "All", "Active", or "Blocked"
     * @param sortBy field to sort by
     * @param page page number (0-based)
     * @param pageSize number of users per page
     * @return list of users matching criteria
     */
    public abstract List<User> getAllUsers(String searchQuery, String statusFilter, String sortBy, int page, int pageSize);

    /**
     * Get total count of users matching criteria
     * @param searchQuery search term
     * @param statusFilter status filter
     * @return total count
     */
    public abstract int getTotalUsersCount(String searchQuery, String statusFilter);

    /**
     * Block a user account
     * @param userId user to block
     * @param adminId admin performing the action
     * @param reason reason for blocking
     * @return true if successful
     */
    public abstract boolean blockUser(Long userId, Long adminId, String reason);

    /**
     * Unblock a user account
     * @param userId user to unblock
     * @param adminId admin performing the action
     * @return true if successful
     */
    public abstract boolean unblockUser(Long userId, Long adminId);

    /**
     * Delete a user account permanently
     * @param userId user to delete
     * @param adminId admin performing the action
     * @return true if successful
     */
    public abstract boolean deleteUser(Long userId, Long adminId);

    /**
     * Get user activity statistics
     * @param userId user ID
     * @return UserActivity object with statistics
     */
    public abstract UserActivity getUserActivity(Long userId);

    /**
     * Get block history for a user
     * @param userId user ID
     * @return list of block records
     */
    public abstract List<BlockRecord> getBlockHistory(Long userId);

    /**
     * Check if user is the sole admin of any groups
     * @param userId user ID
     * @return true if user is sole admin
     */
    public abstract boolean isUserSoleAdminOfGroups(Long userId);

    /**
     * Get user by ID
     * @param userId user ID
     * @return User object or null
     */
    public abstract User getUserById(Long userId);

    /**
     * Update user's last login timestamp
     * @param userId user ID
     */
    public abstract void updateLastLogin(Long userId);

    /**
     * Check if user is an admin
     * @param userId user ID
     * @return true if user is admin
     */
    public abstract boolean isAdmin(Long userId);

    /**
     * Log admin action
     * @param adminId admin ID
     * @param action action type
     * @param targetUserId target user ID
     * @param details action details
     */
    public abstract void logAdminAction(Long adminId, String action, Long targetUserId, String details);
}

