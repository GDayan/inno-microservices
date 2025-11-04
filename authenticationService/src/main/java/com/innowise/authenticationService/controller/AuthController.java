package com.innowise.authenticationService.controller;

import com.innowise.authenticationService.dto.request.AuthRequest;
import com.innowise.authenticationService.dto.request.TokenRequest;
import com.innowise.authenticationService.dto.response.JwtResponse;
import com.innowise.authenticationService.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody TokenRequest tokenRequest) {
        boolean valid = authService.validateToken(tokenRequest.getToken());
        return ResponseEntity.ok(valid);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequest tokenRequest) {
        String newToken = authService.refreshToken(tokenRequest.getToken());
        return ResponseEntity.ok(new JwtResponse(newToken));
    }
}

