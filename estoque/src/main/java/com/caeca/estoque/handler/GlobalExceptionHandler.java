package com.caeca.estoque.handler;

import com.caeca.estoque.exception.ErrorResponse;
import com.caeca.estoque.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFoundHandler(NotFoundException exception){
        ErrorResponse response = new ErrorResponse(
                404, exception.getMessage()
        );
        return ResponseEntity.status(404).body(response);
    }
}
