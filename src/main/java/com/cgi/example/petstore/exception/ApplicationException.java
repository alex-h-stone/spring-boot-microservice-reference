package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends ServiceException {

    public ApplicationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
