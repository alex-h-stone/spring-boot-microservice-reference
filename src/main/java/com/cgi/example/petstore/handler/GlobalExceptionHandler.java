package com.cgi.example.petstore.handler;

import com.cgi.example.petstore.exception.ServiceException;
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

    @ExceptionHandler(ServiceException.class)
    public ProblemDetail onApplicationException(ServiceException serviceException) {
        return logAndCreateProblemDetail(serviceException.getResponseMessage(),
                serviceException.getHttpResponseCode());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail onException(Exception exception) {
        return logAndCreateProblemDetail(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Error.class)
    public ProblemDetail onError(Error error) {
        return logAndCreateProblemDetail(error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ProblemDetail logAndCreateProblemDetail(String detail, HttpStatusCode httpStatus) {
        String className = getClass().getSimpleName();
        String detailedMessage = "Handled by %s - [%s]".formatted(className, detail);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detailedMessage);

        log.info("ProblemDetail: [{}]", problemDetail);
        return problemDetail;
    }
}
