package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

/**
 * Domain event published when the payment has failed and the stock has been released.
 */
public class OrderFailedEvent extends OrderEvent {
    public OrderFailedEvent(Order order) {
        super(OrderEventType.ORDER_FAILED, order);
    }
}
