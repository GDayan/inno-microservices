package com.innowise.authenticationService.dto.response;

public class JwtResponse {
    private String accessToken;

    public JwtResponse() {}

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}

