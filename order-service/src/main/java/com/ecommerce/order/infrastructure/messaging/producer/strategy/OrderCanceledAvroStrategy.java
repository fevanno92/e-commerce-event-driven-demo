package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCanceledAvroEvent;
import com.ecommerce.order.application.outbox.payload.OrderCanceledPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class OrderCanceledAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderCanceledAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_CANCELLED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            OrderCanceledPayload orderCanceledPayload = objectMapper.readValue(payload, OrderCanceledPayload.class);
            
            return OrderCanceledAvroEvent.newBuilder()
                    .setOrderId(orderCanceledPayload.getOrderId())
                    .setReason(orderCanceledPayload.getReason())
                    .setCreatedAt(orderCanceledPayload.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to OrderCanceledAvroEvent", e);
            throw new RuntimeException("Mapping error for OrderCanceledAvroEvent", e);
        }
    }
}
