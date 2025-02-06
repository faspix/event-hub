package com.faspix.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ExceptionResponse {

    private String status;

    private String message;

    private String errors;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ExceptionResponse(HttpStatus httpStatus, Throwable exception) {
        this.status = httpStatus.getReasonPhrase();
        this.message = exception.getMessage();
        this.errors = "todo_errors_list";//Arrays.toString(exception.getStackTrace());
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ExceptionResponse() {
    }
}