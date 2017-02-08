package com.oracle;

public class User {
    private String userId;
    private String userName;
    private String lastActive;
    
    public User() {
        userId = "";
        userName = "";
        lastActive = "";
    }
    
    public User(String userId, String userName, String lastActive) {
        this.userId = userId;
        this.userName = userName;
        this.lastActive = lastActive;
    }
    
    public String getUserId() {
        return this.userId;
    }
    public String getUserName() {
        return this.userName;
    }
    public String getLastActive() {
        return this.lastActive;
    }
    
    public String toString() {
        return "User ID: " + this.userId
            + " User Name: " + this.userName
            + " Last Active: " + this.lastActive;
    }
}
