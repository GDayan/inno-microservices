package com.innowise.authenticationService.controller;

import com.innowise.authenticationService.model.dto.request.LoginRequest;
import com.innowise.authenticationService.model.dto.request.RefreshRequest;
import com.innowise.authenticationService.model.dto.request.RegisterRequest;
import com.innowise.authenticationService.model.dto.response.TokenResponse;
import com.innowise.authenticationService.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        TokenResponse tokens = authService.login(req);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest req) {
        TokenResponse tokens = authService.refresh(req);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam("token") String token) {
        boolean ok = authService.validateAccessToken(token);
        return ResponseEntity.ok().body(Collections.singletonMap("valid", ok));
    }
}
