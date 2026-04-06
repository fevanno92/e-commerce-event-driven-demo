package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderCompletedPayload;
import com.ecommerce.order.domain.event.OrderCompletedEvent;
import com.ecommerce.order.domain.event.OrderEvent;

@Component
public class OrderCompletedPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderCompletedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderCompletedEvent completedEvent = (OrderCompletedEvent) event;
        return OrderCompletedPayload.builder()
                .orderId(completedEvent.getOrder().getId().getValue())
                .customerId(completedEvent.getOrder().getCustomerId().getValue())
                .createdAt(Instant.now())
                .build();
    }
}
