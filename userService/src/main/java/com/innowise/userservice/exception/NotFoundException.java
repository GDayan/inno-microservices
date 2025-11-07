package com.innowise.userservice.exception;

import com.innowise.userservice.util.Constant;

/**
 * Exception thrown when a requested resource is not found.
 * Used for 404 Not Found responses.
 */
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String resource, Long id) {
        super(String.format(Constant.NOT_FOUND_BY_ID, resource, id));
    }

    public NotFoundException(String resource, String identifier) {
        super(String.format("%s not found: %s", resource, identifier));
    }
}