package com.innowise.exception;

public class UserAlreadyExistException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private static final String USER_WITH_EMAIL_EXIST = "User with email: %s already exists";
    public UserAlreadyExistException(String email) {
        super(USER_WITH_EMAIL_EXIST.formatted(email));
    }
}
