package com.ecommerce.common.outbox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * Generic processor for outbox messages.
 * Orchestrates the fetching, publication, and status updates of messages.
 * Uses parallel async sends with CompletableFuture for improved throughput.
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
     * Sends all messages in parallel for improved throughput, then waits for all
     * acknowledgments before updating database statuses within the same transaction.
     * 
     * @param batchSize Maximum number of messages to process.
     * @return true if messages were processed.
     */
    public boolean processBatch(int batchSize) {
        List<OutboxMessage> messages = outboxRepository.findAndLockPendingMessages(batchSize);

        if (messages.isEmpty()) {
            return false;
        }

        log.info("Processing batch of {} outbox messages", messages.size());

        // Create a record to track each message and its future
        List<MessagePublishResult> results = new ArrayList<>(messages.size());

        // Phase 1: Send all messages in parallel (non-blocking)
        for (OutboxMessage message : messages) {
            CompletableFuture<Void> future = outboxMessagePublisher.publish(message);
            results.add(new MessagePublishResult(message, future));
        }

        // Phase 2: Wait for all sends to complete and collect results
        int successCount = 0;
        for (MessagePublishResult result : results) {
            if (awaitAndProcessResult(result)) {
                successCount++;
            }
        }

        log.info("Batch complete: {}/{} messages published successfully", successCount, messages.size());
        return true;
    }

    /**
     * Waits for a single message's publish future to complete and updates the database status.
     * 
     * @param result The message and its associated future
     * @return true if the message was published successfully
     */
    private boolean awaitAndProcessResult(MessagePublishResult result) {
        OutboxMessage message = result.message();
        try {
            // Wait for this specific message's acknowledgment
            result.future().join();
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

    /**
     * Record to hold a message and its async publish result.
     */
    private record MessagePublishResult(OutboxMessage message, CompletableFuture<Void> future) {}
}
