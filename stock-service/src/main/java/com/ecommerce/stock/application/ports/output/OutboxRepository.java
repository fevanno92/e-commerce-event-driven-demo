package com.ecommerce.stock.application.ports.output;

import java.util.List;
import java.util.UUID;

import com.ecommerce.stock.application.outbox.OutboxMessage;
import com.ecommerce.stock.application.outbox.OutboxStatus;

/**
 * Outbound port for managing outbox messages.
 * This interface defines the contract for persisting events
 * in the outbox table, enabling reliable publication to Kafka.
 */
public interface OutboxRepository {

    /**
     * Save a new outbox message to the database.
     *
     * @param message The message to persist
     */
    void save(OutboxMessage message);

    /**
     * Retrieve and lock messages pending publication.
     * Uses database-level locking to prevent multiple instances from processing the same messages.
     * Must be called within a transaction - the lock is held until the transaction completes.
     *
     * @param batchSize Maximum number of messages to retrieve and lock
     * @return List of locked messages with PENDING status
     */
    List<OutboxMessage> findAndLockPendingMessages(int batchSize);

    /**
     * Update the status of an outbox message.
     *
     * @param id The message identifier
     * @param status The new status
     */
    void updateStatus(UUID id, OutboxStatus status);

    /**
     * Increment the retry count of a message without changing its status.
     * Used when a retry will be attempted later.
     *
     * @param id The message identifier
     */
    void incrementRetryCount(UUID id);

    /**
     * Delete messages that have been successfully processed after a certain period.
     * This helps to clean up the outbox table.
     *
     * @param retentionDays Number of days to retain messages with PROCESSED status before deletion
     */
    void deleteProcessedMessages(int retentionDays);
}
