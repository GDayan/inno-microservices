package com.innowise.authenticationService.model.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
