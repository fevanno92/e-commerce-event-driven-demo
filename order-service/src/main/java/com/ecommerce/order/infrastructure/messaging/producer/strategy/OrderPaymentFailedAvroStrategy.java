package com.ecommerce.order.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderPaymentFailedAvroEvent;
import com.ecommerce.order.application.outbox.payload.OrderPaymentFailedPayload;
import com.ecommerce.order.domain.event.OrderEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class OrderPaymentFailedAvroStrategy implements OrderOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public OrderPaymentFailedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return OrderEventType.ORDER_PAYMENT_FAILED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            OrderPaymentFailedPayload orderPaymentFailedPayload = objectMapper.readValue(payload, OrderPaymentFailedPayload.class);
            
            return OrderPaymentFailedAvroEvent.newBuilder()
                    .setOrderId(orderPaymentFailedPayload.orderId())
                    .setCustomerId(orderPaymentFailedPayload.customerId())
                    .setReason(orderPaymentFailedPayload.reason())
                    .setCreatedAt(orderPaymentFailedPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to OrderPaymentFailedAvroEvent", e);
            throw new RuntimeException("Mapping error for OrderPaymentFailedAvroEvent", e);
        }
    }
}
