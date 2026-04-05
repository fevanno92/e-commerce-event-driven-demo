package com.ecommerce.common.outbox;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Generic processor for outbox messages.
 * Orchestrates the fetching, publication, and status updates of messages.
 */
@Slf4j
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final OutboxMessagePublisher outboxMessagePublisher;
    private final int maxRetryCount;

    public OutboxProcessor(OutboxRepository outboxRepository, 
                           OutboxMessagePublisher outboxMessagePublisher,
                           int maxRetryCount) {
        this.outboxRepository = outboxRepository;
        this.outboxMessagePublisher = outboxMessagePublisher;
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Processes a batch of pending messages. 
     * This should typically be called within a transaction by the caller.
     * @param batchSize Maximum number of messages to process.
     * @return true if messages were processed.
     */
    public boolean processBatch(int batchSize) {
        List<OutboxMessage> messages = outboxRepository.findAndLockPendingMessages(batchSize);

        if (messages.isEmpty()) {
            return false;
        }

        log.info("Processing batch of {} outbox messages", messages.size());

        int successCount = 0;
        for (OutboxMessage message : messages) {
            if (processMessage(message)) {
                successCount++;
            }
        }

        log.info("Batch complete: {}/{} messages published successfully", successCount, messages.size());
        return true;
    }

    private boolean processMessage(OutboxMessage message) {
        try {
            outboxMessagePublisher.publish(message);
            outboxRepository.updateStatus(message.getId(), OutboxStatus.PROCESSED);
            return true;
        } catch (Exception e) {
            log.error("Failed to publish outbox message {}: {}", message.getId(), e.getMessage());

            int newRetryCount = message.getRetryCount() + 1;
            if (newRetryCount >= maxRetryCount) {
                log.error("Message {} has exceeded max retry count ({}), marking as failed",
                        message.getId(), maxRetryCount);
                outboxRepository.updateStatus(message.getId(), OutboxStatus.FAILED);
            } else {
                outboxRepository.incrementRetryCount(message.getId());
            }
            return false;
        }
    }
}
