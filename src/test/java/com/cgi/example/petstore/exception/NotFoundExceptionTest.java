package com.cgi.example.petstore.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NotFoundExceptionTest {

    @Test
    void given_NotFoundException_should_HaveMessageAndNotFoundHttpStatusCode() {
        NotFoundException exception = new NotFoundException("Not found error message");

        assertAll(
                () -> assertEquals("Not found error message", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpResponseCode())
        );
    }
}