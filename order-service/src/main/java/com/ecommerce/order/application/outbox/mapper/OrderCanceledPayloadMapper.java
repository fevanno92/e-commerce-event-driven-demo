package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderCanceledPayload;
import com.ecommerce.order.domain.event.OrderEvent;
import com.ecommerce.order.domain.event.OrderCanceledEvent;

@Component
public class OrderCanceledPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderCanceledEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderCanceledEvent orderCanceledEvent = (OrderCanceledEvent) event;
        return OrderCanceledPayload.builder()
                .orderId(orderCanceledEvent.getOrder().getId().getValue())
                .reason(orderCanceledEvent.getReason())
                .createdAt(Instant.now())
                .build();
    }
}
