package com.syncstudy.BL.AdminManager;

import com.syncstudy.BL.SessionManager.User;
import java.util.List;

/**
 * Singleton Facade providing simplified interface for admin operations
 * Used by UI controllers to interact with admin functionality
 */
public class AdminFacade {
    private static AdminFacade instance;
    private AdminManager adminManager;
    private Long currentAdminId;

    private AdminFacade() {
        this.adminManager = AdminManager.getInstance();
    }

    /**
     * Get the singleton instance of AdminFacade
     * @return AdminFacade instance
     */
    public static AdminFacade getInstance() {
        if (instance == null) {
            synchronized (AdminFacade.class) {
                if (instance == null) {
                    instance = new AdminFacade();
                }
            }
        }
        return instance;
    }

    /**
     * Set the current admin user ID
     * @param adminId admin user ID
     */
    public void setCurrentAdminId(Long adminId) {
        this.currentAdminId = adminId;
    }

    /**
     * Get the current admin user ID
     * @return current admin ID
     */
    public Long getCurrentAdminId() {
        return currentAdminId;
    }

    /**
     * Get all users with filtering and pagination
     * @param searchQuery search term for name or email
     * @param statusFilter "All", "Active", or "Blocked"
     * @param sortBy field to sort by
     * @param page page number (0-based)
     * @param pageSize users per page
     * @return list of users
     */
    public List<User> getAllUsers(String searchQuery, String statusFilter, String sortBy, int page, int pageSize) {
        return adminManager.getAllUsers(searchQuery, statusFilter, sortBy, page, pageSize);
    }

    /**
     * Get all users with default pagination (page 0, 20 per page)
     * @param searchQuery search term
     * @param statusFilter status filter
     * @param sortBy sort field
     * @return list of users
     */
    public List<User> getAllUsers(String searchQuery, String statusFilter, String sortBy) {
        return getAllUsers(searchQuery, statusFilter, sortBy, 0, 20);
    }

    /**
     * Get total users count for pagination
     * @param searchQuery search term
     * @param statusFilter status filter
     * @return total count
     */
    public int getTotalUsersCount(String searchQuery, String statusFilter) {
        return adminManager.getTotalUsersCount(searchQuery, statusFilter);
    }

    /**
     * Block a user account
     * @param userId user to block
     * @param reason reason for blocking
     * @return true if successful
     * @throws IllegalStateException if no admin is logged in
     */
    public boolean blockUser(Long userId, String reason) {
        checkAdminLoggedIn();
        return adminManager.blockUser(userId, currentAdminId, reason);
    }

    /**
     * Unblock a user account
     * @param userId user to unblock
     * @return true if successful
     * @throws IllegalStateException if no admin is logged in
     */
    public boolean unblockUser(Long userId) {
        checkAdminLoggedIn();
        return adminManager.unblockUser(userId, currentAdminId);
    }

    /**
     * Delete a user account permanently
     * @param userId user to delete
     * @return true if successful
     * @throws IllegalStateException if no admin is logged in
     */
    public boolean deleteUser(Long userId) {
        checkAdminLoggedIn();
        return adminManager.deleteUser(userId, currentAdminId);
    }

    /**
     * Get user activity statistics
     * @param userId user ID
     * @return UserActivity object
     */
    public UserActivity getUserActivity(Long userId) {
        return adminManager.getUserActivity(userId);
    }

    /**
     * Get block history for a user
     * @param userId user ID
     * @return list of block records
     */
    public List<BlockRecord> getBlockHistory(Long userId) {
        return adminManager.getBlockHistory(userId);
    }

    /**
     * Check if user is sole admin of any groups
     * @param userId user ID
     * @return true if sole admin
     */
    public boolean isUserSoleAdminOfGroups(Long userId) {
        return adminManager.isUserSoleAdminOfGroups(userId);
    }

    /**
     * Get user by ID
     * @param userId user ID
     * @return User object or null
     */
    public User getUserById(Long userId) {
        return adminManager.getUserById(userId);
    }

    /**
     * Check if current user is an admin
     * @return true if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        if (currentAdminId == null) {
            return false;
        }
        return adminManager.isAdmin(currentAdminId);
    }

    /**
     * Check if a specific user is an admin
     * @param userId user ID to check
     * @return true if user is admin
     */
    public boolean isAdmin(Long userId) {
        return adminManager.isAdmin(userId);
    }

    /**
     * Verify admin is logged in
     * @throws IllegalStateException if no admin logged in
     */
    private void checkAdminLoggedIn() {
        if (currentAdminId == null) {
            throw new IllegalStateException("No admin logged in. Call setCurrentAdminId first.");
        }
    }
}

