package com.syncstudy.BL.AdminManager;

import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.BL.SessionManager.User;
import com.syncstudy.PL.PostgresFactory;

import java.util.List;

/**
 * Singleton AdminManager handling admin-related business logic
 * Manages user administration operations
 */
public class AdminManager {
    private static AdminManager instance;
    private AdminDAO adminDAO;

    private AdminManager() {
        AbstractFactory factory = new PostgresFactory();
        this.adminDAO = factory.createAdminDAO();
    }

    /**
     * Get the singleton instance of AdminManager
     * @return AdminManager instance
     */
    public static AdminManager getInstance() {
        if (instance == null) {
            synchronized (AdminManager.class) {
                if (instance == null) {
                    instance = new AdminManager();
                }
            }
        }
        return instance;
    }

    /**
     * Get all users with filtering and pagination
     * @param searchQuery search term
     * @param statusFilter status filter
     * @param sortBy sort field
     * @param page page number
     * @param pageSize page size
     * @return list of users
     */
    public List<User> getAllUsers(String searchQuery, String statusFilter, String sortBy, int page, int pageSize) {
        return adminDAO.getAllUsers(
                searchQuery != null ? searchQuery.trim() : "",
                statusFilter != null ? statusFilter : "All",
                sortBy != null ? sortBy : "name",
                page,
                pageSize
        );
    }

    /**
     * Get total users count for pagination
     * @param searchQuery search term
     * @param statusFilter status filter
     * @return total count
     */
    public int getTotalUsersCount(String searchQuery, String statusFilter) {
        return adminDAO.getTotalUsersCount(
                searchQuery != null ? searchQuery.trim() : "",
                statusFilter != null ? statusFilter : "All"
        );
    }

    /**
     * Block a user account
     * @param userId user to block
     * @param adminId admin performing action
     * @param reason reason for blocking
     * @return true if successful
     * @throws IllegalArgumentException if validation fails
     * @throws SecurityException if admin lacks permissions
     */
    public boolean blockUser(Long userId, Long adminId, String reason) {
        // Validate inputs
        if (userId == null || adminId == null) {
            throw new IllegalArgumentException("User ID and Admin ID are required");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason for blocking is required");
        }

        // Check admin permissions
        if (!isAdmin(adminId)) {
            throw new SecurityException("Only administrators can block users");
        }

        // Check if user exists
        User user = adminDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Check if already blocked
        if (user.isBlocked()) {
            throw new IllegalArgumentException("User is already blocked");
        }

        // Prevent blocking self
        if (userId.equals(adminId)) {
            throw new IllegalArgumentException("Cannot block your own account");
        }

        // Perform block
        boolean success = adminDAO.blockUser(userId, adminId, reason.trim());

        if (success) {
            // Log the action
            adminDAO.logAdminAction(adminId, "BLOCK_USER", userId, "Reason: " + reason);
            System.out.println("User " + userId + " blocked by admin " + adminId);
        }

        return success;
    }

    /**
     * Unblock a user account
     * @param userId user to unblock
     * @param adminId admin performing action
     * @return true if successful
     * @throws IllegalArgumentException if validation fails
     * @throws SecurityException if admin lacks permissions
     */
    public boolean unblockUser(Long userId, Long adminId) {
        // Validate inputs
        if (userId == null || adminId == null) {
            throw new IllegalArgumentException("User ID and Admin ID are required");
        }

        // Check admin permissions
        if (!isAdmin(adminId)) {
            throw new SecurityException("Only administrators can unblock users");
        }

        // Check if user exists
        User user = adminDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Check if actually blocked
        if (!user.isBlocked()) {
            throw new IllegalArgumentException("User is not blocked");
        }

        // Perform unblock
        boolean success = adminDAO.unblockUser(userId, adminId);

        if (success) {
            adminDAO.logAdminAction(adminId, "UNBLOCK_USER", userId, "Account unblocked");
            System.out.println("User " + userId + " unblocked by admin " + adminId);
        }

        return success;
    }

    /**
     * Delete a user account permanently
     * @param userId user to delete
     * @param adminId admin performing action
     * @return true if successful
     * @throws IllegalArgumentException if validation fails
     * @throws SecurityException if admin lacks permissions
     */
    public boolean deleteUser(Long userId, Long adminId) {
        // Validate inputs
        if (userId == null || adminId == null) {
            throw new IllegalArgumentException("User ID and Admin ID are required");
        }

        // Check admin permissions
        if (!isAdmin(adminId)) {
            throw new SecurityException("Only administrators can delete users");
        }

        // Check if user exists
        User user = adminDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Prevent deleting self
        if (userId.equals(adminId)) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }

        // Check if sole admin of groups
        if (adminDAO.isUserSoleAdminOfGroups(userId)) {
            throw new IllegalArgumentException("User is the only admin of one or more groups. Reassign admin roles first.");
        }

        // Log before deletion (capture user info)
        String userSummary = "Username: " + user.getUsername() + ", Email: " + user.getEmail();

        // Perform deletion
        boolean success = adminDAO.deleteUser(userId, adminId);

        if (success) {
            adminDAO.logAdminAction(adminId, "DELETE_USER", userId, "Deleted user: " + userSummary);
            System.out.println("User " + userId + " deleted by admin " + adminId);
        }

        return success;
    }

    /**
     * Get user activity statistics
     * @param userId user ID
     * @return UserActivity object
     */
    public UserActivity getUserActivity(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        return adminDAO.getUserActivity(userId);
    }

    /**
     * Get block history for a user
     * @param userId user ID
     * @return list of block records
     */
    public List<BlockRecord> getBlockHistory(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        return adminDAO.getBlockHistory(userId);
    }

    /**
     * Check if user is sole admin of any groups
     * @param userId user ID
     * @return true if sole admin
     */
    public boolean isUserSoleAdminOfGroups(Long userId) {
        return adminDAO.isUserSoleAdminOfGroups(userId);
    }

    /**
     * Get user by ID
     * @param userId user ID
     * @return User object or null
     */
    public User getUserById(Long userId) {
        return adminDAO.getUserById(userId);
    }

    /**
     * Check if user is an admin
     * @param userId user ID
     * @return true if admin
     */
    public boolean isAdmin(Long userId) {
        return adminDAO.isAdmin(userId);
    }
}

