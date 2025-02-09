package com.faspix.exception;

public class CategoryAlreadyExistException extends RuntimeException {
  public CategoryAlreadyExistException(String message) {
    super(message);
  }
}
