package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;

public class ValidationExceptionAbstract extends AbstractApplicationException {

    public ValidationExceptionAbstract(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
