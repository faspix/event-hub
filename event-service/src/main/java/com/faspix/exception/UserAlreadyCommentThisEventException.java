package com.faspix.exception;

public class UserAlreadyCommentThisEventException extends RuntimeException {
    public UserAlreadyCommentThisEventException(String message) {
        super(message);
    }
}
