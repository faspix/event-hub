package com.faspix.exception;

public class SearchServiceException extends RuntimeException {
    public SearchServiceException(String message) {
        super(message);
    }

    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
