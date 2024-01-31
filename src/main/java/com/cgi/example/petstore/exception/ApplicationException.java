package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ApplicationException extends RuntimeException implements ServiceException {

    @Override
    public HttpStatusCode getHttpResponseCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getResponseMessage() {
        return getMessage();
    }
}
