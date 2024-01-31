package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatusCode;

public interface ServiceException {

    HttpStatusCode getHttpResponseCode();

    String getResponseMessage();
}
