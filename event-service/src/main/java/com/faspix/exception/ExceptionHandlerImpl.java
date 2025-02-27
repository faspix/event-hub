package com.faspix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleUserNotFoundException(final EventNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ExceptionResponse handleUserNotFoundException(final ValidationException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ExceptionResponse handleUserAlreadyCommentThisEventException(final UserAlreadyCommentThisEventException e) {
        return new ExceptionResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ExceptionResponse handleGeneralException(final Exception e) {
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

}
