package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderCanceledPayload;
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
        return new OrderCanceledPayload(
                orderCanceledEvent.getOrder().getId().getValue().toString(),
                orderCanceledEvent.getReason(),
                Instant.now()
        );
    }
}
