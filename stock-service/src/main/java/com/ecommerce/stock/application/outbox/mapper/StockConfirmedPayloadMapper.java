package com.ecommerce.stock.application.outbox.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.stock.application.outbox.payload.StockConfirmedPayload;
import com.ecommerce.stock.domain.event.StockConfirmedEvent;
import com.ecommerce.stock.domain.event.StockEvent;

import java.time.Instant;

@Component
public class StockConfirmedPayloadMapper implements StockEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends StockEvent> eventClass) {
        return StockConfirmedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(StockEvent event) {
        StockConfirmedEvent confirmedEvent = (StockConfirmedEvent) event;
        return new StockConfirmedPayload(
                confirmedEvent.getStockReservation().getOrderId().getValue().toString(),
                Instant.now() // Or from reservation if available
        );
    }
}
