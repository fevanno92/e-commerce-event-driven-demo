package com.ecommerce.stock.application.outbox;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.stock.application.outbox.payload.StockItemPayload;
import com.ecommerce.stock.application.outbox.payload.StockReservedPayload;
import com.ecommerce.stock.application.outbox.payload.StockUnavailablePayload;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.event.StockReservedEvent;
import com.ecommerce.stock.domain.event.StockUnavailableEvent;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Serializes domain events to JSON for storage in the outbox.
 */
@Slf4j
@Component
public class OutboxEventSerializer {

    private final ObjectMapper objectMapper;

    public OutboxEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Create an OutboxMessage from a StockEvent.
     */
    public OutboxMessage toOutboxMessage(StockEvent event) {
        try {
            Object payload = switch (event) {
                case StockReservedEvent reservedEvent -> mapToReservedPayload(reservedEvent);
                case StockUnavailableEvent unavailableEvent -> mapToUnavailablePayload(unavailableEvent);
                default -> event; // Fallback to raw event if unknown
            };

            String payloadJson = objectMapper.writeValueAsString(payload);
            
            return OutboxMessage.create(
                    "Stock",
                    event.getStockReservation().getOrderId().getId().toString(),
                    event.getEventType().getValue(),
                    payloadJson
            );
        } catch (Exception e) {
            log.error("Could not serialize event of type {}", event.getClass().getName(), e);
            throw new RuntimeException("Could not serialize event", e);
        }
    }

    private StockReservedPayload mapToReservedPayload(StockReservedEvent event) {
        List<StockItemPayload> items = event.getStockReservation().getItems().stream()
                .map(item -> StockItemPayload.builder()
                        .productId(item.getProductId().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return StockReservedPayload.builder()
                .orderId(event.getStockReservation().getOrderId().getId())
                .items(items)
                .createdAt(Instant.now())
                .build();
    }

    private StockUnavailablePayload mapToUnavailablePayload(StockUnavailableEvent event) {
        return StockUnavailablePayload.builder()
                .orderId(event.getStockReservation().getOrderId().getId())
                .reason(event.getReason())
                .createdAt(Instant.now())
                .build();
    }
}
