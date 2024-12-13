package com.tunemate.be.global.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private final int errorCode;
    private final HttpStatus statusCode;
    private final String errorMessage;

    public CustomException(String message, HttpStatus statusCode, int errorCode, String errorMessage) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
