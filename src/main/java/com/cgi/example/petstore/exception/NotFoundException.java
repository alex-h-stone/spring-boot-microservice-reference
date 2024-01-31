package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NotFoundException extends RuntimeException implements ServiceException {

    public NotFoundException(String message){
        super(message);
    }

    @Override
    public HttpStatusCode getHttpResponseCode() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getResponseMessage() {
        return getMessage();
    }
}
