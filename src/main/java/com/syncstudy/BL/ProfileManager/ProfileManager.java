package com.syncstudy.BL.ProfileManager;

import com.syncstudy.BL.AbstractFactory;
import com.syncstudy.BL.SessionManager.UserManager;
import com.syncstudy.PL.PostgresFactory;

import java.util.List;

/**
 * Singleton ProfileManager handling profile-related business logic
 */
public class ProfileManager {
    private static ProfileManager instance;
    private ProfileDAO profileDAO;

    private ProfileManager() {
        // Initialize with concrete factory (can be changed for other DB types)
        AbstractFactory factory = new PostgresFactory();
        this.profileDAO = factory.createProfileDAO();
    }

    /**
     * Get the singleton instance of ProfileManager
     * @return ProfileManager instance
     */
    public static ProfileManager getInstance() {
        if (instance == null) {
            synchronized (ProfileManager.class) {
                if (instance == null) {
                    instance = new ProfileManager();
                }
            }
        }
        return instance;
    }

    public boolean updateProfile(Long profileId, Long userId, String firstname, String lastname) {
        if (profileId == null ||
                userId == null ||
                firstname == null || firstname.trim().isEmpty() ||
                lastname == null || lastname.trim().isEmpty()) {
            return false;
        }
        return profileDAO.updateProfile(profileId,userId,firstname,lastname);
    }

    public UserProfile findProfileByUserId(Long userId) {
        return profileDAO.findProfileByUserId(userId);
    }

    public List<UserProfile> findAllProfiles(String searchQuery, String sortBy, int page, int pageSize) {
        return profileDAO.findAllProfiles(searchQuery, sortBy, page, pageSize);
    }


}
