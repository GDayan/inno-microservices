package com.innowise.authenticationService.service;

import com.innowise.authenticationService.model.User;

public interface AuthService {

    /**
     * Registers a new user with the given username and password.
     * The password will be automatically hashed using BCrypt.
     *
     * @param username The username of the new user
     * @param rawPassword The plaintext password of the new user
     * @return The saved User entity
     */
    User register(String username, String rawPassword);

    /**
     * Authenticates a user using username and password.
     *
     * @param username The username of the user
     * @param rawPassword The plaintext password of the user
     * @return JWT access token
     * @throws RuntimeException if the credentials are invalid
     */
    String login(String username, String rawPassword);

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token
     * @return A new access token
     * @throws RuntimeException if the refresh token is invalid
     */
    String refreshToken(String refreshToken);
}

