package com.ecommerce.common.outbox;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for publishing outbox messages to a messaging system (e.g., Kafka).
 * Implementations should return a CompletableFuture to enable parallel batch processing.
 */
public interface OutboxMessagePublisher {
    
    /**
     * Publishes a message asynchronously to the messaging system.
     * 
     * @param message the outbox message to publish
     * @return a CompletableFuture that completes when the message is acknowledged
     */
    CompletableFuture<Void> publish(OutboxMessage message);
}
