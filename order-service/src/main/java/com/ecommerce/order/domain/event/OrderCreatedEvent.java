package com.ecommerce.order.domain.event;

import com.ecommerce.common.domain.event.DomainEvent;
import com.ecommerce.order.domain.entity.Order;

public class OrderCreatedEvent implements DomainEvent {
    private final Order order;

    public OrderCreatedEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

}
