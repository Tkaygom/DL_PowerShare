package com.firsthachathoners.powershare;

public class SessionUpdateRequest {
    private String username;
    private String sessionAction; // e.g., "start", "end"

    // Constructor, getters, and setters
    public SessionUpdateRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSessionAction() { return sessionAction; }
    public void setSessionAction(String sessionAction) { this.sessionAction = sessionAction; }
}