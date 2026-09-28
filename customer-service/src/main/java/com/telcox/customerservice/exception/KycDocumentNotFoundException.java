package com.telcox.customerservice.exception;

public class KycDocumentNotFoundException extends RuntimeException {
    public KycDocumentNotFoundException(String message) {
        super(message);
    }
}
