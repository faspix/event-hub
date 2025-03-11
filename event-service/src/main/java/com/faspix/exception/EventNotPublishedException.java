package com.faspix.exception;

public class EventNotPublishedException extends RuntimeException {
  public EventNotPublishedException(String message) {
    super(message);
  }
}
