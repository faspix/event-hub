package com.faspix.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

    private String status;

    private String message;

//    private String errors;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ExceptionResponse(HttpStatus httpStatus, Throwable exception) {
        this.status = httpStatus.getReasonPhrase();
        this.message = exception.getMessage();
//        this.errors = "todo_errors_list";//Arrays.toString(exception.getStackTrace());
        this.timestamp = LocalDateTime.now();
    }

}