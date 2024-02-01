package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatusCode;

public abstract class ServiceException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public ServiceException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpResponseCode() {
        return httpStatusCode;
    }

    public String getResponseMessage() {
        return getMessage();
    }
}
