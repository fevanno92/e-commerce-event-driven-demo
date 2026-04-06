package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderFailedAvroEvent;
import com.ecommerce.common.event.payload.OrderFailedPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class OrderFailedAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderFailedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_FAILED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            OrderFailedPayload orderFailedPayload = objectMapper.readValue(payload, OrderFailedPayload.class);
            
            return OrderFailedAvroEvent.newBuilder()
                    .setOrderId(orderFailedPayload.orderId().toString())
                    .setCustomerId(orderFailedPayload.customerId().toString())                      
                    .setCreatedAt(orderFailedPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to OrderFailedAvroEvent", e);
            throw new RuntimeException("Mapping error for OrderFailedAvroEvent", e);
        }
    }
}
