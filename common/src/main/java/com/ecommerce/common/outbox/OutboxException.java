package com.ecommerce.common.outbox;

public class OutboxException extends RuntimeException {
    public OutboxException(String message) {
        super(message);
    }
}
