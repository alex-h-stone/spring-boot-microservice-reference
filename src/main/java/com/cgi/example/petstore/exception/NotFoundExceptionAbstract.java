package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;

public class NotFoundExceptionAbstract extends AbstractApplicationException {

    public NotFoundExceptionAbstract(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
