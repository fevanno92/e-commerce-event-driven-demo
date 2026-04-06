package com.ecommerce.stock.domain.event;


public enum StockEventType {

    STOCK_UNAVAILABLE("StockUnavailableEvent"),
    STOCK_RESERVED("StockReservedEvent"),
    STOCK_CONFIRMED("StockConfirmedEvent"),
    STOCK_RELEASED("StockReleasedEvent");

    private final String value;

    StockEventType(String value) { 
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
