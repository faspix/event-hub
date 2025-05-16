package com.faspix.exception;

import com.faspix.shared.exception.ExceptionResponse;
import com.faspix.shared.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleCategoryNotFoundException(final CategoryNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ExceptionResponse handleValidationException(final ValidationException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ExceptionResponse handleCategoryAlreadyExistException(final CategoryAlreadyExistException e) {
        return new ExceptionResponse(HttpStatus.CONFLICT.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ExceptionResponse handleCategoryNotEmptyException(final CategoryNotEmptyException e) {
        return new ExceptionResponse(HttpStatus.CONFLICT.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleResourceNotFoundException(final ResourceNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ExceptionResponse handleGeneralException(final Exception e) {
        e.printStackTrace();
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e);
    }

}
