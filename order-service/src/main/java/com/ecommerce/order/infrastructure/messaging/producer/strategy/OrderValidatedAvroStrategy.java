package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderValidatedAvroEvent;
import com.ecommerce.common.event.payload.OrderValidatedPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class OrderValidatedAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderValidatedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_VALIDATED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            OrderValidatedPayload orderValidatedPayload = objectMapper.readValue(payload, OrderValidatedPayload.class);
            
            return OrderValidatedAvroEvent.newBuilder()
                    .setOrderId(orderValidatedPayload.orderId().toString())
                    .setCustomerId(orderValidatedPayload.customerId().toString())
                    .setTotalAmount(orderValidatedPayload.totalAmount())
                    .setCreatedAt(orderValidatedPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to OrderValidatedAvroEvent", e);
            throw new RuntimeException("Mapping error for OrderValidatedAvroEvent", e);
        }
    }
}
