package com.ecommerce.payment.application.outbox.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ecommerce.payment.application.outbox.payload.PaymentFailedPayload;
import com.ecommerce.payment.domain.event.PaymentEvent;
import com.ecommerce.payment.domain.event.PaymentFailedEvent;

@Component
public class PaymentFailedPayloadMapper implements PaymentEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends PaymentEvent> eventClass) {
        return PaymentFailedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(PaymentEvent event) {
        // TODO
        PaymentFailedEvent failedEvent = (PaymentFailedEvent) event;
        return PaymentFailedPayload.builder()
                .orderId(UUID.randomUUID())               
                .build();
    }
}
