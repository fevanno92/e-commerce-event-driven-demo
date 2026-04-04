package com.ecommerce.stock.infrastructure.messaging.producer;

import com.ecommerce.stock.application.outbox.OutboxMessage;


public interface OutboxMessagePublisher {
    void publish(OutboxMessage message);
}
