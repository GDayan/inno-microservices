package com.innowise.userservice.exception;

import com.innowise.userservice.util.Constant;

public class UserAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistException(String email) {
        super(String.format(Constant.USER_ALREADY_EXISTS, email));
    }

    public UserAlreadyExistException(String email, Throwable cause) {
        super(String.format(Constant.USER_ALREADY_EXISTS, email), cause);
    }

    public UserAlreadyExistException(String message, String email) {
        super(message);
    }
}