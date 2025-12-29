package com.syncstudy.BL.AdminManager;

import java.time.LocalDateTime;

/**
 * Entity representing user activity statistics
 * Used for displaying user activity in admin panel
 */
public class UserActivity {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String profilePhoto;
    private int messagesCount;
    private int filesCount;
    private int groupsCount;
    private int sessionsCreated;
    private int sessionsAttended;
    private int accountAgeDays;
    private LocalDateTime lastLogin;
    private double avgMessagesPerDay;
    private String mostActiveGroup;
    private int engagementScore;
    private boolean isBlocked;
    private LocalDateTime registrationDate;

    public UserActivity() {
    }

    public UserActivity(Long userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(int messagesCount) {
        this.messagesCount = messagesCount;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    public int getGroupsCount() {
        return groupsCount;
    }

    public void setGroupsCount(int groupsCount) {
        this.groupsCount = groupsCount;
    }

    public int getSessionsCreated() {
        return sessionsCreated;
    }

    public void setSessionsCreated(int sessionsCreated) {
        this.sessionsCreated = sessionsCreated;
    }

    public int getSessionsAttended() {
        return sessionsAttended;
    }

    public void setSessionsAttended(int sessionsAttended) {
        this.sessionsAttended = sessionsAttended;
    }

    public int getAccountAgeDays() {
        return accountAgeDays;
    }

    public void setAccountAgeDays(int accountAgeDays) {
        this.accountAgeDays = accountAgeDays;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public double getAvgMessagesPerDay() {
        return avgMessagesPerDay;
    }

    public void setAvgMessagesPerDay(double avgMessagesPerDay) {
        this.avgMessagesPerDay = avgMessagesPerDay;
    }

    public String getMostActiveGroup() {
        return mostActiveGroup;
    }

    public void setMostActiveGroup(String mostActiveGroup) {
        this.mostActiveGroup = mostActiveGroup;
    }

    public int getEngagementScore() {
        return engagementScore;
    }

    public void setEngagementScore(int engagementScore) {
        this.engagementScore = engagementScore;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Get display name
     * @return full name or username
     */
    public String getDisplayName() {
        return fullName != null && !fullName.isEmpty() ? fullName : username;
    }

    /**
     * Calculate engagement score based on activity
     */
    public void calculateEngagementScore() {
        int score = 0;
        score += messagesCount * 2;
        score += filesCount * 5;
        score += groupsCount * 10;
        score += sessionsCreated * 15;
        score += sessionsAttended * 8;

        // Normalize to 0-100
        this.engagementScore = Math.min(100, score / 10);
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", engagementScore=" + engagementScore +
                '}';
    }
}

