package com.syncstudy.BL.SessionManager;
import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.PL.PostgresFactory;

import java.sql.SQLException;

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

    /**
     * Create a user with given data
     * @param username the username of the user we want to create
     * @param passwordHash a password hash for the new user
     * @param email the new user's email
     * @param fullName the new user's full name
     * @param university the new user's university
     * @param department the new user's department
     * @return true if user creation is successful, false otherwise
     * @throws SQLException if an error occurs during the data insertion process
     */
    public Long createUser(String username, String passwordHash, String email, String fullName, String university, String department) throws SQLException {
        if (username == null || username.trim().isEmpty() ||
                passwordHash == null || passwordHash.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                university == null || university.trim().isEmpty() ||
                department == null || department.trim().isEmpty()) {
            return null;
        }
        return userDAO.createUser(username,passwordHash,email,fullName,university,department);
    }

    /**
     * Delete a user by id
     * @param userId the id to delete the user from
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteUser(Long userId) {
        if (userId == null) {
            return false;
        }
        return userDAO.deleteUser(userId);
    }

    /**
     * Find a user by id
     * @param userId the id to search for
     * @return User object if found, null otherwise
     */
    public User findUserById(Long userId) {
        return userDAO.findUserById(userId);
    }



}
