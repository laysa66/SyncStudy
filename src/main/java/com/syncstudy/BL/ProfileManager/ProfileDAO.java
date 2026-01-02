package com.syncstudy.BL.ProfileManager;

import java.util.List;

/**
 * Abstract Data Access Object for Profile operations
 * Defines the contract for persistence operations without specifying DB type
 */
public abstract class ProfileDAO {

    public abstract boolean updateProfile(Long profileId, Long userId, String firstname, String lastname);
    public abstract UserProfile findProfileByUserId(Long userId);
    public abstract List<UserProfile> findAllProfiles(String searchQuery, String sortBy, int page, int pageSize);
}
