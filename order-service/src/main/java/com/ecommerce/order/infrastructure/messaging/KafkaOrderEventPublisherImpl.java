package com.ecommerce.order.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.order.application.dto.OrderDTO;
import com.ecommerce.order.application.mapper.OrderDataMapper;
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
    private final OrderDataMapper orderDataMapper;

    public KafkaOrderEventPublisherImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, OrderDataMapper orderDataMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
       try {
            OrderDTO payload = orderDataMapper.orderToOrderDTO(event.getOrder());
            String payloadJson = objectMapper.writeValueAsString(payload);
            log.info("Publishing OrderCreatedEvent to Kafka with payload: {}", payloadJson);
            // it would be more efficient to use a callback instead of blocking get() to be notified of the success/failure, but for simplicity we will block here
            kafkaTemplate.send(TOPIC, event.getOrder().getId().toString(), payloadJson).get();
            log.info("Published OrderCreated for orderId={}", event.getOrder().getId());
        } catch (Exception e) {
            log.error("Kafka publish failed", e.getCause());
            throw new RuntimeException("Failed to publish OrderCreatedEvent to Kafka: " + e.getMessage(), e);
        }
    }
    
}
