package com.ecommerce.order.domain.event;

import com.ecommerce.order.domain.entity.Order;

public class OrderCanceledEvent extends OrderEvent {
    private final String reason;

    public OrderCanceledEvent(Order order, String reason) {
        super(OrderEventType.ORDER_CANCELLED, order);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
