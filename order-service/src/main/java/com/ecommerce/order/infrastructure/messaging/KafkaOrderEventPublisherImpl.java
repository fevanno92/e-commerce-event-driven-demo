package com.ecommerce.order.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCreatedAvroEvent;
import com.ecommerce.order.application.ports.output.OrderEventPublisher;
import com.ecommerce.order.domain.event.OrderCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaOrderEventPublisherImpl implements OrderEventPublisher {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, OrderCreatedAvroEvent> kafkaTemplate;

    public KafkaOrderEventPublisherImpl(KafkaTemplate<String, OrderCreatedAvroEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        try {
            // Map domain event to Avro event
            OrderCreatedAvroEvent avroEvent = mapToAvroEvent(event);
                        
            // Send Avro serialized message to Kafka
            kafkaTemplate.send(TOPIC, event.getOrder().getId().toString(), avroEvent).get();

            log.info("Published OrderCreatedEvent for orderId={}", event.getOrder().getId());
        } catch (Exception e) {
            log.error("Kafka publish failed", e.getCause());
            throw new RuntimeException("Failed to publish OrderCreatedEvent to Kafka: " + e.getMessage(), e);
        }
    }

    /**
     * Map domain OrderCreatedEvent to Avro OrderCreatedEvent
     */
    private OrderCreatedAvroEvent mapToAvroEvent(OrderCreatedEvent event) {
        var avroItems = new java.util.ArrayList<com.ecommerce.common.avro.event.OrderItem>();
        for (var item : event.getOrder().getItems()) {
            avroItems.add(com.ecommerce.common.avro.event.OrderItem.newBuilder()
                    .setProductId(item.getProductId().getId().toString())
                    .setQuantity(item.getQuantity())
                    .setPrice(item.getPrice().getAmount().doubleValue())
                    .build());
        }

        return OrderCreatedAvroEvent.newBuilder()
                .setOrderId(event.getOrder().getId().getId().toString())
                .setCustomerId(event.getOrder().getCustomerId().getId().toString())
                .setOrderStatus(event.getOrder().getStatus().toString())
                .setCreatedAt(event.getOrder().getCreatedAt().toEpochMilli())
                .setItems(avroItems)
                .build();
    }
}



