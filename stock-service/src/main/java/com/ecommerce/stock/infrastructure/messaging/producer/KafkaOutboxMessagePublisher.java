package com.ecommerce.stock.infrastructure.messaging.producer;

import org.springframework.stereotype.Component;

import com.ecommerce.stock.application.outbox.OutboxEventType;
import com.ecommerce.stock.application.outbox.OutboxMessage;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Implementation of OutboxMessagePublisher that publishes messages to Kafka.
 */
@Component
@Slf4j
public class KafkaOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String STOCK_EVENTS_TOPIC = "stock-events";

    private final ObjectMapper objectMapper;

    public KafkaOutboxMessagePublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxMessage message) {
        try {
            OutboxEventType eventType = OutboxEventType.fromValue(message.getEventType());

            switch (eventType) {
                case STOCK_UNAVAILABLE -> publishStockUnreservedEvent(message);
                case STOCK_RESERVED -> publishStockReservedEvent(message);
                case STOCK_RELEASED -> publishStockReleasedEvent(message);
            }
        } catch (IllegalArgumentException e) {
            log.error("Unknown event type in outbox message {}: {}", message.getId(), message.getEventType());
            throw e;
        } catch (Exception e) {
            log.error("Failed to publish outbox message {}", message.getId(), e);
            throw new RuntimeException("Failed to publish message to Kafka: " + e.getMessage(), e);
        }
    }

    private void publishStockUnreservedEvent(OutboxMessage message) throws Exception {
    }

    private void publishStockReservedEvent(OutboxMessage message) throws Exception {
    }

    private void publishStockReleasedEvent(OutboxMessage message) throws Exception {
    }
}
