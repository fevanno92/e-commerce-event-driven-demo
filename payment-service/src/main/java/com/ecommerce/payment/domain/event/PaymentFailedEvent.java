package com.ecommerce.payment.domain.event;

public class PaymentFailedEvent extends PaymentEvent {
    public PaymentFailedEvent() {
        super(PaymentEventType.PAYMENT_FAILED);
    }
}
