package com.innowise.userservice.exception;

public class UserAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistException(String message, String email) {
        super(String.format("%s: %s", message, email));
    }
}