package com.ecommerce.order.infrastructure.messaging.producer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.outbox.OutboxMessagePublisher;
import com.ecommerce.order.infrastructure.messaging.producer.strategy.OrderOutboxMessageStrategy;

import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
// Jackson imports removed as we use original JSON payload

@Component
@Profile("aws")
@Slf4j
public class AwsOrderOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String ORDER_EVENTS_TOPIC = "order-events.fifo";

    private final SnsTemplate snsTemplate;
    private final List<OrderOutboxMessageStrategy> strategies;

    public AwsOrderOutboxMessagePublisher(SnsTemplate snsTemplate, 
                                          List<OrderOutboxMessageStrategy> strategies) {
        this.snsTemplate = snsTemplate;
        this.strategies = strategies;
    }

    @Override
    public CompletableFuture<Void> publish(OutboxMessage message) {
        try {
            // We still use strategies to ensure domain logic/validation is consistent
            Object avroEvent = strategies.stream()
                    .filter(strategy -> strategy.supports(message.getEventType()))
                    .findFirst()
                    .orElseThrow(() -> new OutboxException("No strategy found for event type: " + message.getEventType()))
                    .mapToAvro(message.getPayload());

            // Use the original JSON payload from the outbox message for SNS transmission.
            // This avoids complex Jackson-Avro serialization issues at runtime.
            String jsonPayload = message.getPayload();

            log.info("Publishing event {} to SNS topic {}", message.getEventType(), ORDER_EVENTS_TOPIC);
            
            // SNS Template usually returns void or notification info.
            // We wrap it in CompletableFuture for consistency with the interface.
            return CompletableFuture.runAsync(() -> {
                String subject = avroEvent.getClass().getSimpleName().replace("Avro", "");
                snsTemplate.sendNotification(ORDER_EVENTS_TOPIC, SnsNotification.builder(jsonPayload)
                        .groupId(message.getAggregateId())
                        .subject(subject)
                        .build());
                log.info("Successfully published {} to SNS from outbox message {}", 
                        subject, message.getId());
            });

        } catch (OutboxException e) {
            log.error("Failed to prepare outbox message {} for publishing to SNS", message.getId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
