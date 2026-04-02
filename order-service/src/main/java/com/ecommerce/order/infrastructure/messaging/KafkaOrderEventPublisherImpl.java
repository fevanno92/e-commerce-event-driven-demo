package com.ecommerce.order.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.order.application.ports.output.OrderEventPublisher;
import com.ecommerce.order.domain.event.OrderCreatedEvent;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class KafkaOrderEventPublisherImpl implements OrderEventPublisher {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderEventPublisherImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
       try {
            String payload = objectMapper.writeValueAsString(event);
            // it would be more efficient to use a callback instead of blocking get() to be notified of the success/failure, but for simplicity we will block here
            kafkaTemplate.send(TOPIC, event.getOrder().getId().toString(), payload).get();
            log.info("Published OrderCreated for orderId={}", event.getOrder().getId());
        } catch (Exception e) {
            log.error("Kafka publish failed", e.getCause());
            throw new RuntimeException("Failed to publish OrderCreatedEvent to Kafka: " + e.getMessage(), e);
        }
    }
    
}
