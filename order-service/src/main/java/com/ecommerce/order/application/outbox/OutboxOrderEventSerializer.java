package com.ecommerce.order.application.outbox;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.tracing.TracingContextHandler;
import com.ecommerce.order.application.outbox.mapper.OrderEventPayloadMapper;
import com.ecommerce.order.domain.event.OrderEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Serializes Order domain events to JSON for storage in the outbox.
 * Follows SOLID principles by using injected mappers for each event type.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxOrderEventSerializer {

    private final ObjectMapper objectMapper;
    private final List<OrderEventPayloadMapper> mappers;
    private final TracingContextHandler tracingContextHandler;

    /**
     * Create an OutboxMessage from an OrderEvent.
     */
    public OutboxMessage createOutboxMessage(OrderEvent event) {
        try {
            Object payload = mappers.stream()
                .filter(mapper -> mapper.supports(event.getClass()))
                .findFirst()
                .map(mapper -> mapper.mapToPayload(event))
                .orElseThrow(() -> new OutboxException("No payload mapper found for event class: " + event.getClass().getName()));

            String payloadJson = objectMapper.writeValueAsString(payload);
            
            return OutboxMessage.create(
                    "Order",
                    event.getOrder().getId().getValue().toString(),
                    event.getEventType().getValue(),
                    payloadJson,
                    tracingContextHandler.captureContext()
            );
        } catch (Exception e) {
            log.error("Could not serialize order event", e);
            throw new RuntimeException("Failed to serialize OrderEvent to JSON", e);
        }
    }
}
