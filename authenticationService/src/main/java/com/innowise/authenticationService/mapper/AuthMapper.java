package com.innowise.authenticationService.mapper;

import com.innowise.authenticationService.dto.request.AuthRequest;
import com.innowise.authenticationService.dto.response.JwtResponse;
import com.innowise.authenticationService.model.User;

public class AuthMapper {

    /**
     * Converts AuthRequest DTO to User entity.
     * The password should be hashed separately in the service.
     */
    public static User toUser(AuthRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        // Password hashing happens in AuthService
        return user;
    }

    /**
     * Converts User entity to JwtResponse DTO.
     */
    public static JwtResponse toJwtResponse(String token) {
        return new JwtResponse(token);
    }
}

