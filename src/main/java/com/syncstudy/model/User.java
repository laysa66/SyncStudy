package com.syncstudy.model;

import java.time.LocalDateTime;

public class User {
    
    private Long userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String university;
    private String department;
    private String bio;
    private String profilePicture;
    private UserRole role;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int violationCount;
    
    public User() {
        this.createdAt = LocalDateTime.now();
        this.violationCount = 0;
        this.role = UserRole.STUDENT;
        this.status = AccountStatus.PENDING_VERIFICATION;
    }
    
    public User(String fullName, String email, String passwordHash, String university, String department) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.university = university;
        this.department = department;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getUniversity() {
        return university;
    }
    
    public void setUniversity(String university) {
        this.university = university;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public AccountStatus getStatus() {
        return status;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public int getViolationCount() {
        return violationCount;
    }
    
    public void setViolationCount(int violationCount) {
        this.violationCount = violationCount;
    }
}