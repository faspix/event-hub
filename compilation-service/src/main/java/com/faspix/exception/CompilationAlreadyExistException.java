package com.faspix.exception;

public class CompilationAlreadyExistException extends RuntimeException {
  public CompilationAlreadyExistException(String message) {
    super(message);
  }
}
