package com.ecommerce.stock.application.outbox;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.common.outbox.OutboxException;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.stock.application.outbox.mapper.StockEventPayloadMapper;
import com.ecommerce.stock.domain.event.StockEvent;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Serializes domain events to JSON for storage in the outbox.
 * Follows SOLID principles by using injected mappers for each event type.
 */
@Slf4j
@Component
public class OutboxStockEventSerializer {

    private final ObjectMapper objectMapper;
    private final List<StockEventPayloadMapper> mappers;

    public OutboxStockEventSerializer(ObjectMapper objectMapper, List<StockEventPayloadMapper> mappers) {
        this.objectMapper = objectMapper;
        this.mappers = mappers;
    }

    /**
     * Create an OutboxMessage from a StockEvent.
     */
    public OutboxMessage toOutboxMessage(StockEvent event) {
        try {
            Object payload = mappers.stream()
                .filter(mapper -> mapper.supports(event.getClass()))
                .findFirst()
                .map(mapper -> mapper.mapToPayload(event))
                .orElseThrow(() -> new OutboxException("No payload mapper found for event class: " + event.getClass().getName()));

            String payloadJson = objectMapper.writeValueAsString(payload);
            
            return OutboxMessage.create(
                    "Stock",
                    event.getStockReservation().getId().getValue().toString(),
                    event.getEventType().getValue(),
                    payloadJson
            );
        } catch (Exception e) {
            log.error("Could not serialize event of type {}", event.getClass().getName(), e);
            throw new RuntimeException("Could not serialize event", e);
        }
    }
}
