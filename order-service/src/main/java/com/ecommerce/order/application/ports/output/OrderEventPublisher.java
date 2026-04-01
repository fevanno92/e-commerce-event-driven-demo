package com.ecommerce.order.application.ports.output;

import com.ecommerce.common.domain.event.DomainEvent;

public interface OrderEventPublisher {
    public void publish(DomainEvent event);
}
