package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCreatedAvroEvent;
import com.ecommerce.common.avro.event.OrderItem;
import com.ecommerce.order.application.outbox.payload.OrderCreatedPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Strategy implementation for mapping OrderCreated outbox events to Avro.
 */
@Component
@Slf4j
public class OrderCreatedAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderCreatedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_CREATED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payloadJson) {
        try {
            OrderCreatedPayload payload = objectMapper.readValue(payloadJson, OrderCreatedPayload.class);

            List<OrderItem> avroItems = payload.items().stream()
                    .map(item -> OrderItem.newBuilder()
                            .setProductId(item.productId())
                            .setQuantity(item.quantity())
                            .setPrice(item.price())
                            .build())
                    .toList();

            return OrderCreatedAvroEvent.newBuilder()
                    .setOrderId(payload.orderId())
                    .setCustomerId(payload.customerId())
                    .setOrderStatus(payload.orderStatus())
                    .setCreatedAt(payload.createdAt().toEpochMilli())
                    .setItems(avroItems)
                    .build();
        } catch (Exception e) {
            log.error("Failed to map OrderCreated outbox payload to Avro", e);
            throw new RuntimeException("Mapping error for OrderCreated", e);
        }
    }
}
