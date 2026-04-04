package com.ecommerce.stock.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;

/**
 * Strategy interface for mapping a specific outbox event type to an Avro record.
 * This ensures the KafkaOutboxMessagePublisher follows the Open-Closed Principle.
 */
public interface OutboxMessageStrategy {
    /**
     * @param eventType The type of the event from the outbox message.
     * @return true if this strategy supports the given event type.
     */
    boolean supports(String eventType);

    /**
     * Maps the JSON payload of the outbox message to an Avro record.
     * @param payloadJson The JSON string from the outbox payload.
     * @return The Avro record to be published.     
     */
    SpecificRecordBase mapToAvro(String payloadJson);
}
