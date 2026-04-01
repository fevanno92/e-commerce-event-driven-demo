package com.ecommerce.order.infrastructure.messaging;

import org.springframework.stereotype.Component;

import com.ecommerce.common.domain.event.DomainEvent;
import com.ecommerce.order.application.ports.output.OrderEventPublisher;

@Component
public class KafkaOrderEventPublisherImpl implements OrderEventPublisher {

    @Override
    public void publish(DomainEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'publish'");
    }
    
}
