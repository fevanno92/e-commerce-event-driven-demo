package com.ecommerce.common.outbox;

public interface OutboxMessagePublisher {
    void publish(OutboxMessage message);
}
