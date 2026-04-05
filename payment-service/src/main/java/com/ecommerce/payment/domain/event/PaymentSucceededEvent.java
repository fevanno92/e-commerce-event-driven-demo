package com.ecommerce.payment.domain.event;

import com.ecommerce.payment.domain.entity.Payment;

public class PaymentSucceededEvent extends PaymentEvent {
    public PaymentSucceededEvent(Payment payment) {
        super(PaymentEventType.PAYMENT_SUCCESS, payment);
    }
}
