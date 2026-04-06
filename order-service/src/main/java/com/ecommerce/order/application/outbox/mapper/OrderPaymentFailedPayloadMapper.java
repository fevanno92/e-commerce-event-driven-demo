package com.ecommerce.order.application.outbox.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderPaymentFailedPayload;
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
        return new OrderPaymentFailedPayload(
                failedEvent.getOrder().getId().getValue().toString(),
                failedEvent.getOrder().getCustomerId().getValue().toString(),
                failedEvent.getReason(),
                failedEvent.getOrder().getCreatedAt()
        );
    }
}
