package com.syncstudy.BL.SessionManager;


import com.syncstudy.BL.ProfileManager.ProfileManager;

/**
 * Singleton Facade providing simplified interface for session management
 */
public class SessionFacade {
    private static SessionFacade instance;
    private UserManager userManager;
    private ProfileManager profileManager;
    private Long loggedUserId;

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
     * Logs out current user
     * @return true if logout went well, false otherwise
     */
    public boolean logout() {
        this.loggedUserId = null;
        if (this.loggedUserId == null) {
            return true;
        }
        return false;
        //harmonize with Lysa's logout ?
    }

    /**
     * Get the current logged user
     * @return current User if found, null otherwise
     */
    public User getCurrentUser() {
        return userManager.findUserById(this.loggedUserId);
    }
}