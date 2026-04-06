package com.ecommerce.order.application.outbox.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderPaidPayload;
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
        return new OrderPaidPayload(
                paidEvent.getOrder().getId().getValue().toString(),
                paidEvent.getOrder().getCustomerId().getValue().toString(),
                paidEvent.getOrder().getTotalAmount(),
                paidEvent.getOrder().getCreatedAt()
        );
    }
}
