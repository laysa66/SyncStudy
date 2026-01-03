package com.syncstudy.BL.ProfileManager;

import com.syncstudy.BL.AbstractFactory;
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

    public Long createProfile(Long userId, String firstname, String lastname) {
        if (userId == null ||
                firstname == null || firstname.trim().isEmpty() ||
                lastname == null || lastname.trim().isEmpty()) {
            return null;
        }
        return profileDAO.createProfile(userId,firstname,lastname);
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

    /**
     * Delete a profile by id
     * @param userId the id to delete the user from
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteProfile(Long userId) {
        if (userId == null) {
            return false;
        }
        return profileDAO.deleteProfile(userId);
    }


    public int getTotalProfilesCount(String searchQuery) {
        return profileDAO.getTotalProfilesCount(searchQuery != null ? searchQuery.trim() : "");
    }
}
