package com.cgi.example.petstore.exception.handler;

import com.cgi.example.petstore.exception.AbstractApplicationException;
import com.cgi.example.petstore.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AbstractApplicationException.class)
  public ProblemDetail onApplicationException(AbstractApplicationException exception) {
    ProblemDetail problemDetail =
        createProblemDetail(exception.getResponseMessage(), exception.getHttpResponseCode());

    log.info("An exception occurred: [{}]", exception.getMessage(), exception);
    return problemDetail;
  }

  @ExceptionHandler(ValidationException.class)
  public ProblemDetail onValidationException(ValidationException exception) {
    ProblemDetail problemDetail =
        createProblemDetail(exception.getMessage(), HttpStatus.BAD_REQUEST);

    log.info("An exception occurred: [{}]", exception.getMessage(), exception);
    return problemDetail;
  }

  @ExceptionHandler(Throwable.class)
  public ProblemDetail onThrowable(Throwable throwable) {
    ProblemDetail problemDetail =
        createProblemDetail("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);

    log.info("An exception occurred: [{}]", throwable.getMessage(), throwable);
    return problemDetail;
  }

  private ProblemDetail createProblemDetail(String detail, HttpStatusCode httpStatus) {
    String simpleClassName = getClass().getSimpleName();
    String detailedMessage = "Handled by %s: [%s]".formatted(simpleClassName, detail);

    return ProblemDetail.forStatusAndDetail(httpStatus, detailedMessage);
  }
}
