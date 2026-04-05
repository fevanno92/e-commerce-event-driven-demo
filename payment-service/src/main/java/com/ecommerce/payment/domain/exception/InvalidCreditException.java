package com.ecommerce.payment.domain.exception;

public class InvalidCreditException extends RuntimeException {
    public InvalidCreditException(String message) {
        super(message);
    }
}
