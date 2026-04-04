package com.ecommerce.order.infrastructure.messaging.producer;

import com.ecommerce.order.application.outbox.OutboxMessage;


public interface OutboxMessagePublisher {
    void publish(OutboxMessage message);
}
