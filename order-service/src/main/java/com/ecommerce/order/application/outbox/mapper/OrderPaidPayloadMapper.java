package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderPaidPayload;
import com.ecommerce.order.domain.event.OrderEvent;
import com.ecommerce.order.domain.event.OrderPaidEvent;

@Component
public class OrderPaidPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderPaidEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderPaidEvent paidEvent = (OrderPaidEvent) event;
        return OrderPaidPayload.builder()
                .orderId(paidEvent.getOrder().getId().getValue())
                .customerId(paidEvent.getOrder().getCustomerId().getValue())
                .totalAmount(paidEvent.getOrder().getTotalAmount())
                .createdAt(Instant.now())
                .build();
    }
}
