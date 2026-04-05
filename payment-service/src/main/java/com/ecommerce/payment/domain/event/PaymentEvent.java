package com.ecommerce.payment.domain.event;

import com.ecommerce.common.domain.event.DomainEvent;
import com.ecommerce.payment.domain.entity.Payment;

public class PaymentEvent implements DomainEvent {
    private final PaymentEventType eventType;
    private final Payment payment;
    
    public PaymentEvent(PaymentEventType eventType, Payment payment) {
        this.eventType = eventType;
        this.payment = payment;
    }

    public PaymentEventType getEventType() {
        return eventType;
    }

    public Payment getPayment() {
        return payment;
    }
}
