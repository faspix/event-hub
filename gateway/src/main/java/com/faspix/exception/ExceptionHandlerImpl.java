package com.faspix.exception;

import com.faspix.shared.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Mono<ExceptionResponse> handleEventNotFoundException(final EventNotFoundException e) {
        return Mono.just(new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Mono<ExceptionResponse> handleCompilationNotFoundException(final CompilationNotFoundException e) {
        return Mono.just(new ExceptionResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e));
    }

}
