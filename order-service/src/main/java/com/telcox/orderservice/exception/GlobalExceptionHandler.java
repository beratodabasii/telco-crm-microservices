package com.telcox.orderservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomerNotActiveException.class)
    public ResponseEntity<String> handleCustomerNotActive(CustomerNotActiveException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }


}
