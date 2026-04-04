package com.ecommerce.stock.application.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED
}
