package com.innowise.authenticationService.service.impl;

import com.innowise.authenticationService.model.User;
import com.innowise.authenticationService.repository.UserRepository;
import com.innowise.authenticationService.security.JwtUtils;
import com.innowise.authenticationService.service.AuthService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public User register(String username, String rawPassword) {
        String hashed = passwordEncoder.encode(rawPassword);
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashed);
        return userRepository.save(user);
    }

    @Override
    public String login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtils.generateAccessToken(user.getUsername());
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    @Override
    public String refreshToken(String refreshToken) {
        return jwtUtils.refreshAccessToken(refreshToken);
    }
}
