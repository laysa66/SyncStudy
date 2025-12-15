package com.syncstudy.BL.SessionManager;


/**
 * Abstract Data Access Object for User operations
 * Defines the contract for persistence operations without specifying DB type
 */
public abstract class UserDAO {

    /**
     * Find a user by username
     * @param username the username to search for
     * @return User object if found, null otherwise
     */
    public abstract User findUserByUsername(String username);

    /**
     * Check if the provided credentials are valid
     * @param username the username
     * @param password the plain text password
     * @return true if credentials are valid, false otherwise
     */
    public abstract boolean checkCredentials(String username, String password);
}