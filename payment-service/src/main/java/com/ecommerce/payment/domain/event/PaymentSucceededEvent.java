package com.ecommerce.payment.domain.event;

public class PaymentSucceededEvent extends PaymentEvent {
    public PaymentSucceededEvent() {
        super(PaymentEventType.PAYMENT_SUCCESS);
    }
}
