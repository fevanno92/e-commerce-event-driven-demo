package com.ecommerce.stock.infrastructure.messaging.producer;

import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockItem;
import com.ecommerce.common.avro.event.StockReservedAvroEvent;
import com.ecommerce.common.avro.event.StockUnavailableAvroEvent;
import com.ecommerce.stock.application.outbox.OutboxEventType;
import com.ecommerce.stock.application.outbox.OutboxMessage;
import com.ecommerce.stock.application.outbox.payload.StockReservedPayload;
import com.ecommerce.stock.application.outbox.payload.StockUnavailablePayload;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Implementation of OutboxMessagePublisher that publishes messages to Kafka.
 */
@Component
@Slf4j
public class KafkaOutboxMessagePublisher implements OutboxMessagePublisher {

    private static final String STOCK_EVENTS_TOPIC = "stock-events";

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOutboxMessagePublisher(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate, 
                                        ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxMessage message) {
        try {
            OutboxEventType eventType = OutboxEventType.fromValue(message.getEventType());
            
            SpecificRecordBase avroEvent = switch (eventType) {
                case STOCK_RESERVED -> mapToStockReservedAvro(message.getPayload());
                case STOCK_UNAVAILABLE -> mapToStockUnavailableAvro(message.getPayload());
                default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
            };

            // Use OrderId as the key for ordering guarantee
            kafkaTemplate.send(STOCK_EVENTS_TOPIC, message.getAggregateId(), avroEvent).get();
            
            log.info("Successfully published {} to Kafka from outbox message {}", 
                    eventType, message.getId());

        } catch (Exception e) {
            log.error("Failed to publish outbox message {}", message.getId(), e);
            throw new RuntimeException("Failed to publish message to Kafka: " + e.getMessage(), e);
        }
    }

    private StockReservedAvroEvent mapToStockReservedAvro(String jsonPayload) {
        StockReservedPayload payload = objectMapper.readValue(jsonPayload, StockReservedPayload.class);
        
        List<StockItem> items = payload.getItems().stream()
                .map(item -> StockItem.newBuilder()
                        .setProductId(item.getProductId().toString())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();

        return StockReservedAvroEvent.newBuilder()
                .setOrderId(payload.getOrderId().toString())
                .setCreatedAt(payload.getCreatedAt().toEpochMilli())
                .setItems(items)
                .build();
    }

    private StockUnavailableAvroEvent mapToStockUnavailableAvro(String jsonPayload) {
        StockUnavailablePayload payload = objectMapper.readValue(jsonPayload, StockUnavailablePayload.class);

        return StockUnavailableAvroEvent.newBuilder()
                .setOrderId(payload.getOrderId().toString())
                .setCreatedAt(payload.getCreatedAt().toEpochMilli())
                .setReason(payload.getReason())
                .build();
    }
}
