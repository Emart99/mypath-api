package com.mypath.backend.auth;

public class AuthResponse {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String token;
    public AuthResponse(String token) {
        this.token = token;
    }

}
