package com.ecommerce.stock.domain.event;

import com.ecommerce.stock.domain.entity.StockReservation;

/**
 * Domain event published when stock reservation is finalized/confirmed.
 */
public class StockConfirmedEvent extends StockEvent {
    public StockConfirmedEvent(StockReservation stockReservation) {
        super(StockEventType.STOCK_CONFIRMED, stockReservation);
    }
}
