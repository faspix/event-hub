package com.faspix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ExceptionResponse handleValidationException(final ValidationException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ExceptionResponse handleUserAlreadyExistException(final UserAlreadyExistException e) {
        return new ExceptionResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ExceptionResponse handleGeneralException(final Exception e) {
        e.printStackTrace();
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

}
