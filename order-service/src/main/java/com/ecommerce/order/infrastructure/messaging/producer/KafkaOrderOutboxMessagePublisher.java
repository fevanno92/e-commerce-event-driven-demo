package com.ecommerce.order.infrastructure.messaging.producer;

import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.outbox.OutboxMessagePublisher;
import com.ecommerce.order.infrastructure.messaging.producer.strategy.OrderOutboxMessageStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * Technical implementation of OutboxMessagePublisher for the Order Service.
 * Follows SOLID principles by using injected strategies for Avro mapping.
 */
@Component
@Slf4j
public class KafkaOrderOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String ORDER_EVENTS_TOPIC = "order-events";

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final List<OrderOutboxMessageStrategy> strategies;

    public KafkaOrderOutboxMessagePublisher(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate,
            List<OrderOutboxMessageStrategy> strategies) {
        this.kafkaTemplate = kafkaTemplate;
        this.strategies = strategies;
    }

    @Override
    public void publish(OutboxMessage message) {
        try {
            SpecificRecordBase avroEvent = strategies.stream()
                    .filter(strategy -> strategy.supports(message.getEventType()))
                    .findFirst()
                    .orElseThrow(
                            () -> new OutboxException("No strategy found for event type: " + message.getEventType()))
                    .mapToAvro(message.getPayload());

            // Use AggregateId as the key for ordering guarantee (e.g., OrderId)
            kafkaTemplate.send(ORDER_EVENTS_TOPIC, message.getAggregateId(), avroEvent).get();

            log.info("Successfully published {} to Kafka from outbox message {}",
                    message.getEventType(), message.getId());

        } catch (Exception e) {
            log.error("Failed to publish outbox message {}", message.getId(), e);
            throw new RuntimeException("Failed to publish message to Kafka: " + e.getMessage(), e);
        }
    }
}
