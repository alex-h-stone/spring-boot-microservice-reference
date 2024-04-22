package com.cgi.example.petstore.exception.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cgi.example.petstore.exception.AbstractApplicationException;
import com.cgi.example.petstore.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Tag("unit")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void given_ApplicationException_Should_ReturnPopulatedProblemDetail() {
    AbstractApplicationException applicationException =
        new AbstractApplicationException("Custom error message", HttpStatus.BAD_GATEWAY) {};

    ProblemDetail actualProblemDetail = handler.onApplicationException(applicationException);

    assertEquals(HttpStatus.BAD_GATEWAY.value(), actualProblemDetail.getStatus());
    assertEquals(
        "Handled by GlobalExceptionHandler: [Custom error message]",
        actualProblemDetail.getDetail());
  }

  @Test
  void given_ValidationException_Should_ReturnPopulatedProblemDetail() {
    ValidationException validationException = new ValidationException("Validation failed");

    ProblemDetail actualProblemDetail = handler.onValidationException(validationException);

    assertEquals(HttpStatus.BAD_REQUEST.value(), actualProblemDetail.getStatus());
    assertEquals(
        "Handled by GlobalExceptionHandler: [Validation failed]", actualProblemDetail.getDetail());
  }

  @Test
  void given_Throwable_Should_ReturnPopulatedProblemDetail() {
    Throwable throwable = new RuntimeException("Unexpected error");

    ProblemDetail actualProblemDetail = handler.onThrowable(throwable);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actualProblemDetail.getStatus());
    assertEquals(
        "Handled by GlobalExceptionHandler: [An internal server error occurred.]",
        actualProblemDetail.getDetail());
  }
}
