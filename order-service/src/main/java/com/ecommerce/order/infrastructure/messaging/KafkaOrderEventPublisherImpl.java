package com.ecommerce.order.infrastructure.messaging;

import org.springframework.stereotype.Component;

import com.ecommerce.common.domain.event.DomainEvent;
import com.ecommerce.order.application.ports.output.OrderEventPublisher;

@Component
public class KafkaOrderEventPublisherImpl implements OrderEventPublisher {

    @Override
    public void publish(DomainEvent event) {
        // TODO Implement actual Kafka publishing logic here, e.g. using Spring Kafka or another Kafka client library
        // For now, we just log the event to demonstrate the flow
        System.out.println("Publishing event to Kafka: " + event);
    }
    
}
