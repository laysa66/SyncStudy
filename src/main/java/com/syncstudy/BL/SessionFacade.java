package com.syncstudy.BL;



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
}