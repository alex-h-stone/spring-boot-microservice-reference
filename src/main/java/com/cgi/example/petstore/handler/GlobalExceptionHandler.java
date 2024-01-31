package com.cgi.example.petstore.handler;

import com.cgi.example.petstore.exception.ApplicationException;
import com.cgi.example.petstore.exception.ServiceException;
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

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail onApplicationException(ApplicationException applicationException) {
        return logAndCreateProblemDetail(applicationException);
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail onValidationException(ValidationException validationException) {
        return logAndCreateProblemDetail(validationException);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onException(Exception exception) {
        return logAndCreateProblemDetail(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Error.class)
    public ProblemDetail onError(Error error) {
        return logAndCreateProblemDetail(error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ProblemDetail logAndCreateProblemDetail(ServiceException exception) {
        return logAndCreateProblemDetail(exception.getResponseMessage(), exception.getHttpResponseCode());
    }

    private ProblemDetail logAndCreateProblemDetail(String detail, HttpStatusCode httpStatus) {
        String detailedMessage = "Handled by %s - [%s]".formatted(getClass(), detail);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detailedMessage);

        log.info("ProblemDetail: [{}]", problemDetail);
        return problemDetail;
    }
}
