package com.ecommerce.order.application.outbox.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderCreatedPayload;
import com.ecommerce.common.event.payload.OrderCreatedPayload.OrderItemPayload;
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
                .map(item -> OrderItemPayload.builder()
                        .productId(item.getProductId().getValue())
                        .quantity(item.getQuantity())
                        .price(item.getPrice().getAmount().doubleValue())
                        .build())
                .toList();

        return OrderCreatedPayload.builder()
                .orderId(createdEvent.getOrder().getId().getValue())
                .customerId(createdEvent.getOrder().getCustomerId().getValue())
                .orderStatus(createdEvent.getOrder().getStatus().toString())
                .createdAt(createdEvent.getOrder().getCreatedAt())
                .items(items)
                .build();
    }
}
