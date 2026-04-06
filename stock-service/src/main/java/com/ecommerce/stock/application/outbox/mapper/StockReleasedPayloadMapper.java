package com.ecommerce.stock.application.outbox.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.StockReleasedPayload;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.event.StockReleasedEvent;

import java.time.Instant;

@Component
public class StockReleasedPayloadMapper implements StockEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends StockEvent> eventClass) {
        return StockReleasedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(StockEvent event) {
        StockReleasedEvent releasedEvent = (StockReleasedEvent) event;
        return StockReleasedPayload.builder()
                .orderId(releasedEvent.getStockReservation().getOrderId().getValue())
                .createdAt(Instant.now())
                .build();
    }
}
