package com.ecommerce.payment.application.outbox.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ecommerce.payment.application.outbox.payload.PaymentSucceededPayload;
import com.ecommerce.payment.domain.event.PaymentEvent;
import com.ecommerce.payment.domain.event.PaymentSucceededEvent;

@Component
public class PaymentSucceededPayloadMapper implements PaymentEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends PaymentEvent> eventClass) {
        return PaymentSucceededEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(PaymentEvent event) {
        // TODO
        PaymentSucceededEvent succeededEvent = (PaymentSucceededEvent) event;
        return PaymentSucceededPayload.builder()
                .orderId(UUID.randomUUID())
                .build();
    }
}
