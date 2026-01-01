package com.syncstudy.BL;

import com.syncstudy.BL.ProfileManager.ProfileDAO;
import com.syncstudy.BL.SessionManager.UserDAO;
import com.syncstudy.BL.AdminManager.AdminDAO;

/**
 * Abstract Factory for creating DAO instances
 * Allows switching between different persistence implementations
 */
public abstract class AbstractFactory {

    /**
     * Creates a UserDAO instance
     * @return UserDAO implementation
     */
    public abstract UserDAO createUserDAO();

    /**
     * Creates an AdminDAO instance
     * @return AdminDAO implementation
     */
    public abstract AdminDAO createAdminDAO();

    /**
     * Creates a ProfileDAO instance
     * @return ProfileDA implementation
     */
    public abstract ProfileDAO createProfileDAO();
}