package com.ecommerce.payment.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.PaymentFailedAvroEvent;
import com.ecommerce.payment.application.outbox.payload.PaymentFailedPayload;
import com.ecommerce.payment.domain.event.PaymentEventType;

import tools.jackson.databind.ObjectMapper;

@Component
public class PaymentFailedAvroStrategy implements PaymentOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public PaymentFailedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return PaymentEventType.PAYMENT_FAILED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payloadJson) {
        try {
            PaymentFailedPayload payload = objectMapper.readValue(payloadJson, PaymentFailedPayload.class);
                    
            return PaymentFailedAvroEvent.newBuilder()
                    .setOrderId(payload.getOrderId().toString())
                    .setCustomerId(payload.getCustomerId().toString())
                    .setAmount(payload.getAmount())
                    .setReason(payload.getReason())
                    .setCreatedAt(payload.getCreatedAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to map outbox message to Avro in PaymentFailedAvroStrategy", e);
        }
    }
}
