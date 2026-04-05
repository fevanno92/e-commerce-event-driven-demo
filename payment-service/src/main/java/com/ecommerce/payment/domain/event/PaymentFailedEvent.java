package com.ecommerce.payment.domain.event;

import com.ecommerce.payment.domain.entity.Payment;

public class PaymentFailedEvent extends PaymentEvent {
    private final String failureReason;

    public PaymentFailedEvent(Payment payment, String failureReason) {
        super(PaymentEventType.PAYMENT_FAILED, payment);
        this.failureReason = failureReason;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
