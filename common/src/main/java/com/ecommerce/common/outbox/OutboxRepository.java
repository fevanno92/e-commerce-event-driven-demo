package com.ecommerce.common.outbox;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository {
    
    /**
     * Finds and locks a batch of pending messages for processing.
     * Implementations should ideally use SELECT FOR UPDATE SKIP LOCKED.
     */
    List<OutboxMessage> findAndLockPendingMessages(int batchSize);

    /**
     * Updates the status of an outbox message.
     */
    void updateStatus(UUID id, OutboxStatus status);

    /**
     * Increments the retry count of an outbox message.
     */
    void incrementRetryCount(UUID id);
}
