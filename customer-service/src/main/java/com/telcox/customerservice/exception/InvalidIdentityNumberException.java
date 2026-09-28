package com.telcox.customerservice.exception;

public class InvalidIdentityNumberException extends RuntimeException {
    public InvalidIdentityNumberException(String message) {
        super(message);
    }
}
