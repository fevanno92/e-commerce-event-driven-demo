package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

public class OrderValidatedEvent extends OrderEvent {
    public OrderValidatedEvent(Order order) {
        super(OrderEventType.ORDER_VALIDATED, order);
    }
}
