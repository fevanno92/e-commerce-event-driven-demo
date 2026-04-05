package com.ecommerce.payment.application.outbox;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.payment.application.outbox.mapper.PaymentEventPayloadMapper;
import com.ecommerce.payment.domain.event.PaymentEvent;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Serializes domain events to JSON for storage in the outbox.
 * Follows SOLID principles by using injected mappers for each event type.
 */
@Slf4j
@Component
public class OutboxPaymentEventSerializer {

    private final ObjectMapper objectMapper;
    private final List<PaymentEventPayloadMapper> mappers;

    public OutboxPaymentEventSerializer(ObjectMapper objectMapper, List<PaymentEventPayloadMapper> mappers) {
        this.objectMapper = objectMapper;
        this.mappers = mappers;
    }

    /**
     * Create an OutboxMessage from a PaymentEvent.
     */
    public OutboxMessage toOutboxMessage(PaymentEvent event) {
        try {
            Object payload = mappers.stream()
                .filter(mapper -> mapper.supports(event.getClass()))
                .findFirst()
                .map(mapper -> mapper.mapToPayload(event))
                .orElseThrow(() -> new OutboxException("No payload mapper found for event class: " + event.getClass().getName()));

            String payloadJson = objectMapper.writeValueAsString(payload);
            
            return OutboxMessage.create(
                    "Payment",
                    event.getPayment().getId().getValue().toString(),
                    event.getEventType().getValue(),
                    payloadJson
            );
        } catch (Exception e) {
            log.error("Could not serialize event of type {}", event.getClass().getName(), e);
            throw new RuntimeException("Could not serialize event", e);
        }
    }
}
