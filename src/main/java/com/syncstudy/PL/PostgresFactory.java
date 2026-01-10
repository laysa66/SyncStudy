package com.syncstudy.PL;

import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.BL.AdminManager.AdminDAO;
import com.syncstudy.BL.ProfileManager.ProfileDAO;
import com.syncstudy.BL.SessionManager.UserDAO;
import com.syncstudy.BL.GroupManager.GroupDAO;
import com.syncstudy.PL.AdminManager.AdminDAOPostgres;
import com.syncstudy.PL.ProfileManager.ProfileDAOPostgres;
import com.syncstudy.PL.SessionManager.UserDAOPostgres;
import com.syncstudy.PL.GroupManager.GroupDAOPostgres;

/**
 * Concrete Factory for creating PostgreSQL DAO instances
 */
public class PostgresFactory extends AbstractFactory {

    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public AdminDAO createAdminDAO() {
        return new AdminDAOPostgres();
    }

    @Override
    public GroupDAO createGroupDAO() {
        return new GroupDAOPostgres();
    }

    @Override
    public ProfileDAO createProfileDAO() {return new ProfileDAOPostgres();}
}