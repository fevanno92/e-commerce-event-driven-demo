package com.ecommerce.order.application.ports.output;

import com.ecommerce.order.domain.event.OrderCreatedEvent;

public interface OrderEventPublisher {
    public void publish(OrderCreatedEvent event);
}
