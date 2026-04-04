package com.ecommerce.stock.infrastructure.messaging;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.stock.application.outbox.OutboxMessage;
import com.ecommerce.stock.application.outbox.OutboxStatus;
import com.ecommerce.stock.application.ports.output.OutboxRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Processes outbox messages within a single transaction.
 * The database lock acquired by findAndLockPendingMessages is held
 * for the entire duration of the transaction, preventing other instances
 * from processing the same messages.
 */
@Component
@Slf4j
public class OutboxProcessor {

    private static final int MAX_RETRY_COUNT = 3;

    private final OutboxRepository outboxRepository;
    private final OutboxMessagePublisher outboxMessagePublisher;

    public OutboxProcessor(OutboxRepository outboxRepository, OutboxMessagePublisher outboxMessagePublisher) {
        this.outboxRepository = outboxRepository;
        this.outboxMessagePublisher = outboxMessagePublisher;
    }

    /**
     * Fetches and processes a batch of pending messages.
     * Uses SELECT FOR UPDATE SKIP LOCKED to ensure thread-safety across multiple instances.
     * The entire batch is processed within a single transaction, maintaining the lock.
     *
     * @param batchSize Maximum number of messages to process
     * @return Number of messages processed
     */
    @Transactional
    public int processBatch(int batchSize) {
        List<OutboxMessage> messages = outboxRepository.findAndLockPendingMessages(batchSize);

        if (messages.isEmpty()) {
            return 0;
        }

        log.info("Processing batch of {} outbox messages", messages.size());

        int successCount = 0;
        for (OutboxMessage message : messages) {
            if (processMessage(message)) {
                successCount++;
            }
        }

        log.info("Batch complete: {}/{} messages published successfully", successCount, messages.size());
        return messages.size();
    }

    /**
     * Processes a single message. Returns true if published successfully.
     */
    private boolean processMessage(OutboxMessage message) {
        try {
            outboxMessagePublisher.publish(message);
            outboxRepository.updateStatus(message.getId(), OutboxStatus.PROCESSED);
            log.debug("Successfully published outbox message {}", message.getId());
            return true;

        } catch (Exception e) {
            log.error("Failed to publish outbox message {}: {}", message.getId(), e.getMessage());

            int newRetryCount = message.getRetryCount() + 1;
            if (newRetryCount >= MAX_RETRY_COUNT) {
                log.error("Message {} has exceeded max retry count ({}), marking as failed permanently",
                        message.getId(), MAX_RETRY_COUNT);
                outboxRepository.updateStatus(message.getId(), OutboxStatus.FAILED);
            } else {
                outboxRepository.incrementRetryCount(message.getId());
                log.warn("Message {} will be retried (attempt {}/{})",
                        message.getId(), newRetryCount, MAX_RETRY_COUNT);
            }
            return false;
        }
    }
}
