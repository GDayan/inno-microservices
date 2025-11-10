package com.innowise.authenticationService.model.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}