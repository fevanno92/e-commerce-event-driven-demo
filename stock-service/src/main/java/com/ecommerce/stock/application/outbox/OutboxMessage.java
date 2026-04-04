package com.ecommerce.stock.application.outbox;

import java.time.Instant;
import java.util.UUID;

/**
 * Message stored in the outbox table, representing an event to be published to Kafka.
 * This class encapsulates the details of the event, including its type, payload, and status
 */
public class OutboxMessage {

    private final UUID id;
    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final String payload;
    private final Instant createdAt;
    private OutboxStatus status;
    private Instant processedAt;
    private int retryCount;

    public OutboxMessage(UUID id, String aggregateType, String aggregateId, 
                         String eventType, String payload, Instant createdAt, 
                         OutboxStatus status, Instant processedAt, int retryCount) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.status = status;
        this.processedAt = processedAt;
        this.retryCount = retryCount;
    }

    /**
     * Create a new OutboxMessage with the given details. 
     * The message will be initialized with a PENDING status and a retry count of 0.
     */
    public static OutboxMessage create(String aggregateType, String aggregateId, 
                                        String eventType, String payload) {
        return new OutboxMessage(
            UUID.randomUUID(),
            aggregateType,
            aggregateId,
            eventType,
            payload,
            Instant.now(),
            OutboxStatus.PENDING,
            null,
            0
        );
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public int getRetryCount() {
        return retryCount;
    }
    
}
