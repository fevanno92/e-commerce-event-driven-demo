package com.ecommerce.stock.application.exception;

public class OutboxException extends IllegalArgumentException {
    public OutboxException(String message) {
        super(message);
    }
}
