package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

public class OrderCreatedEvent extends OrderEvent {

    public OrderCreatedEvent(Order order) {
        super(OrderEventType.ORDER_CREATED, order);
    }
}
