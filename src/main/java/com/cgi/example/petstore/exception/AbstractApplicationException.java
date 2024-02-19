package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatusCode;

public abstract class AbstractApplicationException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public AbstractApplicationException(String message, HttpStatusCode httpStatusCode) {
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
