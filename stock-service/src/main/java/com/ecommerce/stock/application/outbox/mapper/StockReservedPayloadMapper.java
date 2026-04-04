package com.ecommerce.stock.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.stock.application.outbox.payload.StockItemPayload;
import com.ecommerce.stock.application.outbox.payload.StockReservedPayload;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.event.StockReservedEvent;

@Component
public class StockReservedPayloadMapper implements StockEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends StockEvent> eventClass) {
        return StockReservedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(StockEvent event) {
        StockReservedEvent reservedEvent = (StockReservedEvent) event;
        return StockReservedPayload.builder()
                .orderId(reservedEvent.getStockReservation().getOrderId().getId())
                .createdAt(Instant.now())
                .items(reservedEvent.getStockReservation().getItems().stream()
                        .map(item -> StockItemPayload.builder()
                                .productId(item.getProductId().getId())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build();
    }
}
