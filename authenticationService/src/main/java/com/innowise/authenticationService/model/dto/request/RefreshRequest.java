package com.innowise.authenticationService.model.dto.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
