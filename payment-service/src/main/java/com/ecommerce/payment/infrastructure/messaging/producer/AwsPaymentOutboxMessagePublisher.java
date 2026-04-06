package com.ecommerce.payment.infrastructure.messaging.producer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.outbox.OutboxMessagePublisher;
import com.ecommerce.payment.infrastructure.messaging.producer.strategy.PaymentOutboxMessageStrategy;

import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
// Jackson imports removed as we use original JSON payload

@Component
@Profile("aws")
@Slf4j
public class AwsPaymentOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    private final SnsTemplate snsTemplate;
    private final List<PaymentOutboxMessageStrategy> strategies;

    public AwsPaymentOutboxMessagePublisher(SnsTemplate snsTemplate, 
                                            List<PaymentOutboxMessageStrategy> strategies) {
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

            log.info("Publishing payment event {} to SNS topic {}", message.getEventType(), PAYMENT_EVENTS_TOPIC);
            
            return CompletableFuture.runAsync(() -> {
                String subject = avroEvent.getClass().getSimpleName().replace("Avro", "");
                snsTemplate.sendNotification(PAYMENT_EVENTS_TOPIC, jsonPayload, subject);
                log.info("Successfully published {} to SNS from outbox message {}", 
                        subject, message.getId());
            });

        } catch (OutboxException e) {
            log.error("Failed to prepare outbox message {} for publishing to SNS", message.getId(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
