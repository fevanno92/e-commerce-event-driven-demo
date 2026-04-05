package com.ecommerce.order.application.outbox.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderCreatedPayload;
import com.ecommerce.order.application.outbox.payload.OrderCreatedPayload.OrderItemPayload;
import com.ecommerce.order.domain.event.OrderCreatedEvent;
import com.ecommerce.order.domain.event.OrderEvent;

/**
 * Mapper for OrderCreatedEvent to its outbox payload.
 */
@Component
public class OrderCreatedPayloadMapper implements OrderEventPayloadMapper {

    @Override
    public boolean supports(Class<? extends OrderEvent> eventClass) {
        return OrderCreatedEvent.class.isAssignableFrom(eventClass);
    }

    @Override
    public Object mapToPayload(OrderEvent event) {
        OrderCreatedEvent createdEvent = (OrderCreatedEvent) event;
        
        List<OrderItemPayload> items = createdEvent.getOrder().getItems().stream()
                .map(item -> new OrderItemPayload(
                        item.getProductId().getId().toString(),
                        item.getQuantity(),
                        item.getPrice().getAmount().doubleValue()))
                .toList();

        return new OrderCreatedPayload(
                createdEvent.getOrder().getId().getId().toString(),
                createdEvent.getOrder().getCustomerId().getId().toString(),
                createdEvent.getOrder().getStatus().toString(),
                createdEvent.getOrder().getCreatedAt().toEpochMilli(),
                items);
    }
}
