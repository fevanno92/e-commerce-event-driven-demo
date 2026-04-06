package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderFailedPayload;
import com.ecommerce.order.domain.event.OrderEvent;
import com.ecommerce.order.domain.event.OrderFailedEvent;

@Component
public class OrderFailedPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderFailedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderFailedEvent failedEvent = (OrderFailedEvent) event;
        return new OrderFailedPayload(
                failedEvent.getOrder().getId().getValue().toString(),
                failedEvent.getOrder().getCustomerId().getValue().toString(),
                Instant.now());
    }
}
