package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AbstractApplicationException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
