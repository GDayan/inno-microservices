package com.innowise.authenticationService.service.impl;

import com.innowise.authenticationService.mapper.UserMapper;
import com.innowise.authenticationService.model.dto.UserDto;
import com.innowise.authenticationService.model.dto.request.LoginRequest;
import com.innowise.authenticationService.model.dto.request.RefreshRequest;
import com.innowise.authenticationService.model.dto.request.RegisterRequest;
import com.innowise.authenticationService.model.dto.response.TokenResponse;
import com.innowise.authenticationService.model.entity.RefreshToken;
import com.innowise.authenticationService.model.entity.User;
import com.innowise.authenticationService.repository.RefreshTokenRepository;
import com.innowise.authenticationService.repository.UserRepository;
import com.innowise.authenticationService.security.JwtUtil;
import com.innowise.authenticationService.service.AuthService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public void register(RegisterRequest req) {
        if (userRepository.existsByLogin(req.getLogin())) {
            throw new IllegalArgumentException("Login already exists");
        }
        UserDto dto = new UserDto();
        dto.setLogin(req.getLogin());
        dto.setPassword(req.getPassword());
        dto.setRole("USER");

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByLogin(req.getLogin());
        if (userOpt.isEmpty()) throw new BadCredentialsException("Invalid credentials");
        User u = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String access = jwtUtil.generateAccessToken(u.getLogin(), u.getRole());
        String refresh = jwtUtil.generateRefreshToken();

        RefreshToken rt = new RefreshToken();
        rt.setToken(refresh);
        rt.setUser(u);
        rt.setExpiryDate(Instant.now().plusSeconds(60L * 60 * 24 * jwtUtil.getRefreshExpirationDays()));
        refreshTokenRepository.deleteByUser(u);
        refreshTokenRepository.save(rt);

        return new TokenResponse(access, refresh);
    }

    @Override
    public TokenResponse refresh(RefreshRequest request) {
        String provided = request.getRefreshToken();
        RefreshToken stored = refreshTokenRepository.findByToken(provided)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (stored.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new BadCredentialsException("Refresh token expired");
        }
        User u = stored.getUser();
        String access = jwtUtil.generateAccessToken(u.getLogin(), u.getRole());
        String refresh = jwtUtil.generateRefreshToken();

        refreshTokenRepository.delete(stored);
        RefreshToken newRt = new RefreshToken();
        newRt.setToken(refresh);
        newRt.setUser(u);
        newRt.setExpiryDate(Instant.now().plusSeconds(60L * 60 * 24 * jwtUtil.getRefreshExpirationDays()));
        refreshTokenRepository.save(newRt);

        return new TokenResponse(access, refresh);
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            jwtUtil.parseToken(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }
}