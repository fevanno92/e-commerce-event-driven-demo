package com.ecommerce.payment.domain.event;

import com.ecommerce.common.domain.event.DomainEvent;

public class PaymentEvent implements DomainEvent {
    private final PaymentEventType eventType;
    
    public PaymentEvent(PaymentEventType eventType) {
        this.eventType = eventType;
    }

    public PaymentEventType getEventType() {
        return eventType;
    }    
}
