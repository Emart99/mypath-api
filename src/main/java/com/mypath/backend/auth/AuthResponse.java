package com.mypath.backend.auth;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AuthResponse {
    String accessToken;
    String refreshToken;



    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }

}
