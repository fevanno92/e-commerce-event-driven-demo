package com.ecommerce.stock.infrastructure.messaging;

import com.ecommerce.stock.application.outbox.OutboxMessage;


public interface OutboxMessagePublisher {
    void publish(OutboxMessage message);
}
