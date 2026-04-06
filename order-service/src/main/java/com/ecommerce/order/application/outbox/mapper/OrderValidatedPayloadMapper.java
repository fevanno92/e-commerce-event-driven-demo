package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderValidatedPayload;
import com.ecommerce.order.domain.event.OrderEvent;
import com.ecommerce.order.domain.event.OrderValidatedEvent;

@Component
public class OrderValidatedPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderValidatedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderValidatedEvent validatedEvent = (OrderValidatedEvent) event;
        return new OrderValidatedPayload(
                validatedEvent.getOrder().getId().getValue().toString(),
                validatedEvent.getOrder().getCustomerId().getValue().toString(),
                validatedEvent.getOrder().getTotalAmount(),
                Instant.now()
        );
    }
}
