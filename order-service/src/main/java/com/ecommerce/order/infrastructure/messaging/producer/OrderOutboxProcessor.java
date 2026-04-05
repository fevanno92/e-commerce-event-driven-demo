package com.ecommerce.order.infrastructure.messaging.producer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.outbox.OutboxProcessor;
import com.ecommerce.order.application.ports.output.OrderOutboxRepository;

/**
 * Technical implementation of the Outbox Processor for the Order Service.
 * Leverages the generic OutboxProcessor logic from the common module.
 */
@Component
public class OrderOutboxProcessor extends OutboxProcessor {

    private static final int MAX_RETRY_COUNT = 3;

    public OrderOutboxProcessor(OrderOutboxRepository outboxRepository,
            KafkaOrderOutboxMessagePublisher kafkaOutboxMessagePublisher) {
        super(outboxRepository, kafkaOutboxMessagePublisher, MAX_RETRY_COUNT);
    }

    /**
     * Processes a batch of pending messages within a single transaction.
     */
    @Override
    @Transactional
    public boolean processBatch(int batchSize) {
        return super.processBatch(batchSize);
    }
}
