package com.innowise.userservice.exception;

public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String format, Object... args) {
        super(String.format(format, args));
    }

    public static BadRequestException invalidEmail(String email) {
        return new BadRequestException("Invalid email format: %s", email);
    }

    public static BadRequestException missingParameter(String parameter) {
        return new BadRequestException("Required parameter is missing: %s", parameter);
    }
}