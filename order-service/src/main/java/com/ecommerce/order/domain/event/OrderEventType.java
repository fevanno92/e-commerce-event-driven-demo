package com.ecommerce.order.domain.event;

public enum OrderEventType {
    ORDER_CREATED("order.created"),
    ORDER_VALIDATED("order.validated"),
    ORDER_PAID("order.paid"),
    ORDER_PAYMENT_FAILED("order.payment-failed"),
    ORDER_CANCELLED("order.cancelled");

    private final String value;

    OrderEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
