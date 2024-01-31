package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ValidationException extends RuntimeException implements ServiceException {

    public ValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatusCode getHttpResponseCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getResponseMessage() {
        return getMessage();
    }
}
