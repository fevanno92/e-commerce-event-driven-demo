package com.ecommerce.order.domain.event;

public enum OrderEventType {
    ORDER_CREATED("order.created"),
    ORDER_VALIDATED("order.validated"),
    ORDER_CANCELLED("order.cancelled");

    private final String value;

    OrderEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
