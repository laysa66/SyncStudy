package com.syncstudy.BL;
import com.syncstudy.PL.PostgresFactory;

/**
 * Singleton UserManager handling user-related business logic
 */
public class UserManager {
    private static UserManager instance;
    private UserDAO userDAO;

    private UserManager() {
        // Initialize with concrete factory (can be changed for other DB types)
       AbstractFactory factory = new PostgresFactory();
       this.userDAO = factory.createUserDAO();
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
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }
        return userDAO.checkCredentials(username, password);
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
