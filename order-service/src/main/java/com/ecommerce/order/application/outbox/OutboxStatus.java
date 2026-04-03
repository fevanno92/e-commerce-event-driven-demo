package com.ecommerce.order.application.outbox;

/**
 * Status of an outbox message.
 */
public enum OutboxStatus {
    /**
     * Message pending publication.
     */
    PENDING,
    
    /**
     * Message published successfully.
     */
    PROCESSED,
    
    /**
     * Message failed to be published (after retries).
     */
    FAILED
}
