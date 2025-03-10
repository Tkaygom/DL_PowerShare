package com.firsthachathoners.powershare;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("username")
    private String username;

    @SerializedName("credit_session")
    private String creditSession;

    public String getUsername() {
        return username;
    }

    public String getCreditSession() {
        return creditSession;
    }
}