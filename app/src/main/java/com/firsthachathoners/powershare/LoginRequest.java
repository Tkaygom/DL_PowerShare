package com.firsthachathoners.powershare;

public class LoginRequest {
    private String username;
    private String password;

    // Constructor, getters, and setters
    public LoginRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}