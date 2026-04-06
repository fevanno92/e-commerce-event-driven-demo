package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

/**
 * Domain event published when an order payment fails.
 */
public class OrderPaymentFailedEvent extends OrderEvent {
    private final String reason;

    public OrderPaymentFailedEvent(Order order, String reason) {
        super(OrderEventType.ORDER_PAYMENT_FAILED, order);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
