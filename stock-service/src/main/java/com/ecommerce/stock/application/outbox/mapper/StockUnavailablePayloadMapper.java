package com.ecommerce.stock.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.stock.application.outbox.payload.StockUnavailablePayload;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.event.StockUnavailableEvent;

@Component
public class StockUnavailablePayloadMapper implements StockEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends StockEvent> eventClass) {
        return StockUnavailableEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(StockEvent event) {
        StockUnavailableEvent unavailableEvent = (StockUnavailableEvent) event;
        return StockUnavailablePayload.builder()
                .orderId(unavailableEvent.getStockReservation().getOrderId().getValue())
                .reason(unavailableEvent.getReason())
                .createdAt(Instant.now())
                .build();
    }
}
