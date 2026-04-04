package com.ecommerce.stock.domain.event;

import com.ecommerce.common.domain.event.DomainEvent;
import com.ecommerce.stock.domain.entity.StockReservation;

public class StockEvent implements DomainEvent {
    private final StockEventType eventType;
    private final StockReservation stockReservation;

    public StockEvent(StockEventType eventType, StockReservation stockReservation) {
        this.eventType = eventType;
        this.stockReservation = stockReservation;
    }

    public StockEventType getEventType() {
        return eventType;
    }

    public StockReservation getStockReservation() {
        return stockReservation;
    }
}
