package com.cgi.example.petstore.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationExceptionTest {

    @Test
    void given_ValidationException_should_HaveMessageAndBadRequestHttpStatusCode() {
        ValidationException exception = new ValidationException("Validation exception error message");

        assertAll(
                () -> assertEquals("Validation exception error message", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpResponseCode())
        );
    }
}