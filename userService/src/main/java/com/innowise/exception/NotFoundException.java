package com.innowise.exception;

import static com.innowise.util.Constant.*;

public class NotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NotFoundException(Long id){
        super(NOT_FOUND_BY_ID.formatted(id));
    }

    public NotFoundException(String email){
        super(NOT_FOUND_BY_EMAIL.formatted(email));
    }
}
