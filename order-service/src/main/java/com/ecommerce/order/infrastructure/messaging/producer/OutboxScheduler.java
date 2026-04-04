package com.ecommerce.order.infrastructure.messaging.producer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.order.application.ports.output.OutboxRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler that triggers outbox message processing.
 * Uses OutboxProcessor which handles locking via SELECT FOR UPDATE SKIP LOCKED,
 * allowing multiple instances to process different messages concurrently without duplicates.
 */
@Component
@Slf4j
public class OutboxScheduler {

    private static final int BATCH_SIZE = 10;
    private static final int RETENTION_DAYS = 7;

    private final OutboxProcessor outboxProcessor;
    private final OutboxRepository outboxRepository;

    public OutboxScheduler(OutboxProcessor outboxProcessor, OutboxRepository outboxRepository) {
        this.outboxProcessor = outboxProcessor;
        this.outboxRepository = outboxRepository;
    }

    /**
     * Processes pending messages in the outbox table.
     * Executed every 5 seconds.
     */
    @Scheduled(fixedDelay = 5000)
    public void processOutboxMessages() {
        outboxProcessor.processBatch(BATCH_SIZE);
    }

    /**
     * Cleans up old processed messages.
     * Executed once a day.
     */
    @Scheduled(cron = "0 0 2 * * *") // 2 AM every day
    public void cleanupProcessedMessages() {
        log.info("Cleaning up processed outbox messages older than {} days", RETENTION_DAYS);
        outboxRepository.deleteProcessedMessages(RETENTION_DAYS);
    }
}
