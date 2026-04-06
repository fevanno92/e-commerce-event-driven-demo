package com.ecommerce.payment.infrastructure.messaging.producer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.outbox.OutboxMessagePublisher;
import com.ecommerce.common.outbox.OutboxProcessor;
import com.ecommerce.payment.application.ports.output.PaymentOutboxRepository;

import com.ecommerce.common.tracing.TracingContextHandler;
import io.micrometer.tracing.Tracer;

/**
 * Technical implementation of the Outbox Processor for the Stock Service.
 * Leverages the generic OutboxProcessor logic from the common module
 * while providing the necessary @Transactional and @Component annotations
 * for Spring integration.
 */
@Component
public class PaymentOutboxProcessor extends OutboxProcessor {

    private static final int MAX_RETRY_COUNT = 3;

    public PaymentOutboxProcessor(PaymentOutboxRepository stockOutboxRepository,
            OutboxMessagePublisher stockOutboxMessagePublisher,
            TracingContextHandler tracingContextHandler,
            Tracer tracer) {
        super(stockOutboxRepository, stockOutboxMessagePublisher, tracingContextHandler, tracer, MAX_RETRY_COUNT);
    }

    /**
     * Processes a batch of pending messages within a single transaction.
     * The database lock acquired by findAndLockPendingMessages is held
     * for the entire duration of the transaction.
     */
    @Override
    @Transactional
    public boolean processBatch(int batchSize) {
        return super.processBatch(batchSize);
    }
}
