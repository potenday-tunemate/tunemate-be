package com.tunemate.be.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private boolean ok;
    private HttpStatus statusCode;
    private int errorCode;
    private String message;


}
