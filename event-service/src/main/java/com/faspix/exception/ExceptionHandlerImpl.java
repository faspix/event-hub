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
    private ExceptionResponse handleEventNotFoundException(final EventNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleCommentNotFoundException(final CommentNotFoundException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleEventNotPublishedException(final EventNotPublishedException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ExceptionResponse handleValidationException(final ValidationException e) {
        return new ExceptionResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ExceptionResponse handleUserAlreadyCommentThisEventException(final UserAlreadyCommentThisEventException e) {
        return new ExceptionResponse(HttpStatus.CONFLICT.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ExceptionResponse handleReactionAlreadyExistException(final ReactionAlreadyExistException e) {
        return new ExceptionResponse(HttpStatus.CONFLICT.getReasonPhrase(), e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ExceptionResponse handleReactionNotExistException(final ReactionNotExistException e) {
        return new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
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
