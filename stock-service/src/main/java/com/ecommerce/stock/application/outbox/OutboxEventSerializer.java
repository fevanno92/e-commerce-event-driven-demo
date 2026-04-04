package com.ecommerce.stock.application.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.stock.domain.event.StockUnavailableEvent;

import tools.jackson.databind.ObjectMapper;

/**
 * Serializes domain events to JSON for storage in the outbox.
 */
@Component
public class OutboxEventSerializer {

    private final ObjectMapper objectMapper;

    @Autowired
    public OutboxEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Create an OutboxMessage from an OrderCreatedEvent.
     */
    public OutboxMessage createOutboxMessage(StockUnavailableEvent event) {
        String payload = serializeOrderCreatedEvent(event);
        return OutboxMessage.create(
                "Order",
                "",
                OutboxEventType.STOCK_UNAVAILABLE,
                payload
        );
    }

    private String serializeOrderCreatedEvent(StockUnavailableEvent event) {
        return "";
    }
}
