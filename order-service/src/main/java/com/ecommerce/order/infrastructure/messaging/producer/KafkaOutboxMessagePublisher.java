package com.ecommerce.order.infrastructure.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCreatedAvroEvent;
import com.ecommerce.common.avro.event.OrderItem;
import com.ecommerce.order.application.outbox.OutboxEventType;
import com.ecommerce.order.application.outbox.OutboxMessage;
import com.ecommerce.order.application.outbox.payload.OrderCreatedPayload;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 *  Implementation of OutboxMessagePublisher that publishes messages to Kafka.
 */
@Component
@Slf4j
public class KafkaOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String ORDER_EVENTS_TOPIC = "order-events";

    private final KafkaTemplate<String, OrderCreatedAvroEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOutboxMessagePublisher(KafkaTemplate<String, OrderCreatedAvroEvent> kafkaTemplate, 
                                        ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxMessage message) {
        try {
            OutboxEventType eventType = OutboxEventType.fromValue(message.getEventType());

            switch (eventType) {
                case ORDER_CREATED -> publishOrderCreatedEvent(message);
            }
        } catch (IllegalArgumentException e) {
            log.error("Unknown event type in outbox message {}: {}", message.getId(), message.getEventType());
            throw e;
        } catch (Exception e) {
            log.error("Failed to publish outbox message {}", message.getId(), e);
            throw new RuntimeException("Failed to publish message to Kafka: " + e.getMessage(), e);
        }
    }

    private void publishOrderCreatedEvent(OutboxMessage message) throws Exception {
        OrderCreatedPayload payload = objectMapper.readValue(message.getPayload(), OrderCreatedPayload.class);

        List<OrderItem> avroItems = payload.items().stream()
                .map(item -> OrderItem.newBuilder()
                        .setProductId(item.productId())
                        .setQuantity(item.quantity())
                        .setPrice(item.price())
                        .build())
                .toList();

        OrderCreatedAvroEvent avroEvent = OrderCreatedAvroEvent.newBuilder()
                .setOrderId(payload.orderId())
                .setCustomerId(payload.customerId())
                .setOrderStatus(payload.orderStatus())
                .setCreatedAt(payload.createdAt())
                .setItems(avroItems)
                .build();

        // it would be more efficient to use the KafkaTemplate's async send and handle callbacks, 
        // but for simplicity we block here to ensure the message is sent before marking it as processed
        kafkaTemplate.send(ORDER_EVENTS_TOPIC, message.getAggregateId(), avroEvent).get();
        log.info("Published OrderCreatedEvent for orderId={} from outbox message {}", 
                message.getAggregateId(), message.getId());
    }
}
