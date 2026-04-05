package com.ecommerce.payment.infrastructure.repository.outbox;

import java.time.Instant;
import java.util.UUID;

import com.ecommerce.common.outbox.OutboxStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * JPA entity representing a message in the outbox table.
 * This table is used to implement the Transactional Outbox pattern,
 * ensuring consistency between business data and event publication.
 */
@Entity
@Table(name = "payment_outbox", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_created_at", columnList = "created_at")
})
public class PaymentOutboxEntity {

    @Id
    private UUID id;

    /**
     * Type of the aggregate (e.g., "Order", "Product").
     */
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    /**
     * Identifier of the concerned aggregate.
     */
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    /**
     * Type of the event (e.g., "OrderCreatedEvent").
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * Event payload in JSON format.
     */
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    /**
     * Creation date of the message.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Status of the outbox message.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    /**
     * Date when the message was processed (successful publication).
     */
    @Column(name = "processed_at")
    private Instant processedAt;

    /**
     * Number of publication attempts.
     */
    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    protected PaymentOutboxEntity() {
    }

    public PaymentOutboxEntity(UUID id, String aggregateType, String aggregateId,
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

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
