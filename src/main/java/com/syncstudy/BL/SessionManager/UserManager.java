package com.syncstudy.BL.SessionManager;
import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.PL.PostgresFactory;

/**
 * Singleton UserManager handling user-related business logic
 */
public class UserManager {
    private static UserManager instance = new UserManager();
    private UserDAO userDAO;
    private User currentUser;
    private UserManager() {
    }

    /**
     * Ensure the DAO (and any other resources) are initialized.
     * Safe to call multiple times.
     */
    private void ensureInitialized() {
        if (userDAO == null) {
            synchronized (this) {
                if (userDAO == null) {
                    AbstractFactory factory = new PostgresFactory();
                    this.userDAO = factory.createUserDAO();
                }
            }
        }
    }

    /**
     * Force initialization from application or server startup.
     */
    public static void init() {
        instance.ensureInitialized();
    }

    /**
     * Get the singleton instance of UserManager
     * @return UserManager instance
     */
    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    /**
     * Check if the provided credentials are valid
     * @param username the username
     * @param password the password
     * @return true if credentials are valid, false otherwise
     */
    public boolean checkCredentials(String username, String password) {
        ensureInitialized();
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }
        // First verify credentials
        boolean valid = userDAO.checkCredentials(username, password);
        if (!valid) {
            return false;
        }
        User user = userDAO.findUserByUsername(username);
        setCurrentUser(user);  // Set the current user after successful authentication
        System.out.println("Current User : "+getCurrentUser());
        return true;
    }

    /**
     * Get the current logged-in user
     * @return the current User object
     * @throws IllegalStateException if no user is logged in
     */
    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return currentUser;
    }

    public User setCurrentUser(User user) {
        this.currentUser = user; return this.currentUser;
    }

    /**
     * Logout the current user
     */
    public void logout() {
        this.currentUser = null;
        System.out.println("User logged out successfully.");
    }

    /**
     * Find a user by username
     * @param username the username to search for
     * @return User object if found, null otherwise
     */
    public User findUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }
}
