package com.ecommerce.order.domain.event;

public enum OrderEventType {
    ORDER_CREATED("order.created");

    private final String value;

    OrderEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
