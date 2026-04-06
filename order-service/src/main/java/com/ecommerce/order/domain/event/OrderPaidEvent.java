package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

/**
 * Domain event published when an order is paid.
 */
public class OrderPaidEvent extends OrderEvent {
    public OrderPaidEvent(Order order) {
        super(OrderEventType.ORDER_PAID, order);
    }
}
