package com.tunemate.be.global.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                ex.getStatusCode(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getErrorMessage()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }


    public ResponseEntity<Object> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR,
                9999, // 커스텀 에러 코드 정의 (알 수 없는 에러)
                "An unexpected error occurred.",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
