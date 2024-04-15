package com.cgi.example.petstore.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractApplicationException {

  public NotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}
