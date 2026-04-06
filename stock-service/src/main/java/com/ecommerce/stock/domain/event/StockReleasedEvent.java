package com.ecommerce.stock.domain.event;

import com.ecommerce.stock.domain.entity.StockReservation;

/**
 * Domain event published when stock reservation is released.
 */
public class StockReleasedEvent extends StockEvent {
    public StockReleasedEvent(StockReservation stockReservation) {
        super(StockEventType.STOCK_RELEASED, stockReservation);
    }
}
