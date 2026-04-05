package com.ecommerce.stock.infrastructure.messaging.producer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxMessagePublisher;
import com.ecommerce.common.outbox.strategy.OutboxMessageStrategy;
import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of OutboxMessagePublisher that publishes messages to Kafka.
 * Follows SOLID principles by using injected strategies for Avro mapping.
 * Returns CompletableFuture for non-blocking batch processing.
 */
@Component
@Slf4j
public class KafkaStockOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String STOCK_EVENTS_TOPIC = "stock-events";

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final List<OutboxMessageStrategy> strategies;

    public KafkaStockOutboxMessagePublisher(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate,
            List<OutboxMessageStrategy> strategies) {
        this.kafkaTemplate = kafkaTemplate;
        this.strategies = strategies;
    }

    @Override
    public CompletableFuture<Void> publish(OutboxMessage message) {
        try {
            SpecificRecordBase avroEvent = strategies.stream()
                    .filter(strategy -> strategy.supports(message.getEventType()))
                    .findFirst()
                    .orElseThrow(
                            () -> new OutboxException("No strategy found for event type: " + message.getEventType()))
                    .mapToAvro(message.getPayload());

            // Use OrderId as the key for ordering guarantee
            return kafkaTemplate.send(STOCK_EVENTS_TOPIC, message.getAggregateId(), avroEvent)
                    .thenAccept(result -> log.info("Successfully published {} to Kafka from outbox message {}",
                            message.getEventType(), message.getId()))
                    .exceptionally(ex -> {
                        log.error("Failed to publish outbox message {}", message.getId(), ex);
                        throw new RuntimeException("Failed to publish message to Kafka: " + ex.getMessage(), ex);
                    });

        } catch (Exception e) {
            log.error("Failed to prepare outbox message {} for publishing", message.getId(), e);
            return CompletableFuture.failedFuture(
                    new RuntimeException("Failed to prepare message for Kafka: " + e.getMessage(), e));
        }
    }
}
