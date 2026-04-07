package com.ecommerce.stock.infrastructure.messaging.producer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.outbox.OutboxMessagePublisher;
import com.ecommerce.stock.infrastructure.messaging.producer.strategy.StockOutboxMessageStrategy;

import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
// Jackson imports removed as we use original JSON payload

@Component
@Profile("aws")
@Slf4j
public class AwsStockOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String STOCK_EVENTS_TOPIC = "stock-events.fifo";

    private final SnsTemplate snsTemplate;
    private final List<StockOutboxMessageStrategy> strategies;

    public AwsStockOutboxMessagePublisher(SnsTemplate snsTemplate, 
                                          List<StockOutboxMessageStrategy> strategies) {
        this.snsTemplate = snsTemplate;
        this.strategies = strategies;
    }

    @Override
    public CompletableFuture<Void> publish(OutboxMessage message) {
        try {
            Object avroEvent = strategies.stream()
                    .filter(strategy -> strategy.supports(message.getEventType()))
                    .findFirst()
                    .orElseThrow(() -> new OutboxException("No strategy found for event type: " + message.getEventType()))
                    .mapToAvro(message.getPayload());

            // Use the original JSON payload from the outbox message for SNS transmission.
            String jsonPayload = message.getPayload();

            log.info("Publishing stock event {} to SNS topic {}", message.getEventType(), STOCK_EVENTS_TOPIC);
            
            return CompletableFuture.runAsync(() -> {
                String subject = avroEvent.getClass().getSimpleName().replace("Avro", "");
                snsTemplate.sendNotification(STOCK_EVENTS_TOPIC, SnsNotification.builder(jsonPayload)
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
