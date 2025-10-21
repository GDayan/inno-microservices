package com.innowise.userservice.exception;

import com.innowise.userservice.util.Constant;

/**
 * Exception thrown when a requested resource is not found.
 * Used for 404 Not Found responses.
 */
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String resource, Long id) {
        super(String.format(Constant.NOT_FOUND_BY_ID, resource, id));
    }

    public NotFoundException(String resource, String identifier) {
        super(String.format("%s not found: %s", resource, identifier));
    }

    public static NotFoundException forUser(Long userId) {
        return new NotFoundException("User", userId);
    }

    public static NotFoundException forUser(String email) {
        return new NotFoundException("User", email);
    }

    public static NotFoundException forCard(Long cardId) {
        return new NotFoundException("Card", cardId);
    }
}