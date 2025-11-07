package com.innowise.userservice.util;

public final class Constant {
    public static final String NOT_FOUND_BY_ID = "Resource not found with ID: %s";
    public static final String USER_ALREADY_EXISTS = "User with email '%s' already exists";
    private Constant() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}