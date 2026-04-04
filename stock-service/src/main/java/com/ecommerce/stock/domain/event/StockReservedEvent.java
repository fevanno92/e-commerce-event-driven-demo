package com.ecommerce.stock.domain.event;

import com.ecommerce.stock.domain.entity.StockReservation;

public class StockReservedEvent extends StockEvent {
    public StockReservedEvent(StockReservation stockReservation) {
        super(StockEventType.STOCK_RESERVED, stockReservation);
    }
}
