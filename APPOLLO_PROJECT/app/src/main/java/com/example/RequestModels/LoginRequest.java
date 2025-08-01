package com.example.RequestModels;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
