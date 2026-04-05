package com.ecommerce.order.application.ports.output;

import com.ecommerce.common.outbox.OutboxMessage;

/**
 * Outbound port for managing outbox messages.
 * This interface defines the contract for persisting events
 * in the outbox table, enabling reliable publication to Kafka.
 * Inherits generic processing methods from the common module.
 */
public interface OrderOutboxRepository extends com.ecommerce.common.outbox.OutboxRepository {

    /**
     * Save a new outbox message to the database.
     *
     * @param message The message to persist
     */
    void save(OutboxMessage message);

    /**
     * Delete messages that have been successfully processed after a certain period.
     * This helps to clean up the outbox table.
     *
     * @param retentionDays Number of days to retain messages with PROCESSED status before deletion
     */
    void deleteProcessedMessages(int retentionDays);
}
