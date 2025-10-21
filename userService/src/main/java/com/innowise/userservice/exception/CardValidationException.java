package com.innowise.userservice.exception;

public class CardValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CardValidationException(String message) {
        super(message);
    }

    public CardValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardValidationException(String format, Object... args) {
        super(String.format(format, args));
    }
}