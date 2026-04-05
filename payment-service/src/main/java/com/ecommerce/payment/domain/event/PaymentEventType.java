package com.ecommerce.payment.domain.event;

public enum PaymentEventType {
    PAYMENT_FAILED("PaymentFailedEvent"),
    PAYMENT_SUCCESS("PaymentSuccessEvent");

    private final String value;

    PaymentEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
