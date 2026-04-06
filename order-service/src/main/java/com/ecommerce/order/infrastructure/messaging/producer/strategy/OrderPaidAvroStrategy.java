package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderPaidAvroEvent;
import com.ecommerce.common.event.payload.OrderPaidPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class OrderPaidAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderPaidAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_PAID.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            OrderPaidPayload orderPaidPayload = objectMapper.readValue(payload, OrderPaidPayload.class);
            
            return OrderPaidAvroEvent.newBuilder()
                    .setOrderId(orderPaidPayload.orderId().toString())
                    .setCustomerId(orderPaidPayload.customerId().toString())
                    .setTotalAmount(orderPaidPayload.totalAmount())
                    .setCreatedAt(orderPaidPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to OrderPaidAvroEvent", e);
            throw new RuntimeException("Mapping error for OrderPaidAvroEvent", e);
        }
    }
}
