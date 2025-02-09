package com.faspix.exception;

public class CategoryNotEmptyException extends RuntimeException {
    public CategoryNotEmptyException(String message) {
        super(message);
    }
}
