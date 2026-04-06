package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCompletedAvroEvent;
import com.ecommerce.common.event.payload.OrderCompletedPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class OrderCompletedAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderCompletedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_COMPLETED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            OrderCompletedPayload orderCompletedPayload = objectMapper.readValue(payload, OrderCompletedPayload.class);
            
            return OrderCompletedAvroEvent.newBuilder()
                    .setOrderId(orderCompletedPayload.orderId().toString())
                    .setCustomerId(orderCompletedPayload.customerId().toString())                      
                    .setCreatedAt(orderCompletedPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to OrderCompletedAvroEvent", e);
            throw new RuntimeException("Mapping error for OrderCompletedAvroEvent", e);
        }
    }
}
