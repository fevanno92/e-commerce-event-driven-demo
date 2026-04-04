package com.ecommerce.stock.application.outbox;


public enum OutboxEventType {

    STOCK_UNAVAILABLE("StockUnavailableEvent"),
    STOCK_RESERVED("StockReservedEvent"),
    STOCK_RELEASED("StockReleasedEvent");

    private final String value;

    OutboxEventType(String value) { 
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OutboxEventType fromValue(String value) {
        for (OutboxEventType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown outbox event type: " + value);
    }
}
