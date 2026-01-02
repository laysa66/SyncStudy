package com.syncstudy.BL.SessionManager;



/**
 * Singleton Facade providing simplified interface for session management
 */
public class SessionFacade {
    private static SessionFacade instance;
    private UserManager userManager;

    private SessionFacade() {
        this.userManager = UserManager.getInstance();
    }

    /**
     * Get the singleton instance of SessionFacade
     * @return SessionFacade instance
     */
    public static SessionFacade getInstance() {
        if (instance == null) {
            synchronized (SessionFacade.class) {
                if (instance == null) {
                    instance = new SessionFacade();
                }
            }
        }
        return instance;
    }

    /**
     * Attempt to login with provided credentials
     * @param username the username
     * @param password the password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        try {
            return userManager.checkCredentials(username, password);
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the current logged-in user
     * @return the current User object
     * @throws IllegalStateException if no user is logged in
     */
    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public void setCurrentUser(User user) {
        userManager.setCurrentUser(user);
    }

    public void logout() {
        userManager.setCurrentUser(null);
    }

    public boolean isLoggedIn() {
        return userManager.getCurrentUser() != null;
    }
}