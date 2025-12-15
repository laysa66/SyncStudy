package com.syncstudy.BL;

import com.syncstudy.BL.SessionManager.UserDAO;

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
}