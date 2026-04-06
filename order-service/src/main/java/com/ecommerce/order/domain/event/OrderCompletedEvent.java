package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

/**
 * Domain event published when the order has been successfully paid and the stock has been confirmed.
 */
public class OrderCompletedEvent extends OrderEvent {
    public OrderCompletedEvent(Order order) {
        super(OrderEventType.ORDER_COMPLETED, order);
    }
}
