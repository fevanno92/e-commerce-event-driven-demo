package com.ecommerce.order.domain.event;

import com.ecommerce.common.domain.event.DomainEvent;
import com.ecommerce.order.domain.entity.Order;

/**
 * Base class for all Order domain events.
 */
public abstract class OrderEvent implements DomainEvent {
    private final OrderEventType eventType;
    private final Order order;

    protected OrderEvent(OrderEventType eventType, Order order) {
        this.eventType = eventType;
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public OrderEventType getEventType() {
        return eventType;
    }
}
