package com.innowise.authenticationService.service;

import com.innowise.authenticationService.model.dto.request.LoginRequest;
import com.innowise.authenticationService.model.dto.request.RefreshRequest;
import com.innowise.authenticationService.model.dto.request.RegisterRequest;
import com.innowise.authenticationService.model.dto.response.TokenResponse;
import com.innowise.authenticationService.model.entity.User;

public interface AuthService {

    /**
     * Регистрация нового пользователя
     * @param req данные регистрации (логин и пароль)
     */
    void register(RegisterRequest req);

    /**
     * Логин пользователя и выдача JWT токенов
     * @param req данные логина
     * @return access и refresh токены
     */
    TokenResponse login(LoginRequest req);

    /**
     * Обновление JWT токенов по refresh-токену
     * @param request refresh токен
     * @return новые access и refresh токены
     */
    TokenResponse refresh(RefreshRequest request);

    /**
     * Проверка валидности access токена
     * @param token access токен
     * @return true если токен валиден
     */
    boolean validateAccessToken(String token);
}

