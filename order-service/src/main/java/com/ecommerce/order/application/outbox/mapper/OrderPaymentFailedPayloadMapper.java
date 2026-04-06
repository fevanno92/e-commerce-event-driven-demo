package com.ecommerce.order.application.outbox.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderPaymentFailedPayload;
import com.ecommerce.order.domain.event.OrderEvent;
import com.ecommerce.order.domain.event.OrderPaymentFailedEvent;

@Component
public class OrderPaymentFailedPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderPaymentFailedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderPaymentFailedEvent failedEvent = (OrderPaymentFailedEvent) event;
        return OrderPaymentFailedPayload.builder()
                .orderId(failedEvent.getOrder().getId().getValue())
                .customerId(failedEvent.getOrder().getCustomerId().getValue())
                .reason(failedEvent.getReason())
                .createdAt(Instant.now())
                .build();
    }
}
