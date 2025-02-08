package com.faspix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ExceptionResponse handleUserNotFoundException(final RequestNotFountException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ExceptionResponse handleValidationException(final ValidationException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ExceptionResponse handleGeneralException(final Exception e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

}
