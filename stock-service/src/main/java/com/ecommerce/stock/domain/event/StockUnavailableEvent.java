package com.ecommerce.stock.domain.event;

import com.ecommerce.stock.domain.entity.StockReservation;

public class StockUnavailableEvent extends StockEvent {

    private final String reason;

    public StockUnavailableEvent(StockReservation stockReservation, String reason) {
        super(StockEventType.STOCK_UNAVAILABLE, stockReservation);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
