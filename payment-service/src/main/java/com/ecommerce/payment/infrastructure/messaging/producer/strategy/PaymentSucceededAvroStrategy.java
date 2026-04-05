package com.ecommerce.payment.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.PaymentSucceededAvroEvent;
import com.ecommerce.payment.application.outbox.payload.PaymentSucceededPayload;
import com.ecommerce.payment.domain.event.PaymentEventType;

import tools.jackson.databind.ObjectMapper;

@Component
public class PaymentSucceededAvroStrategy implements PaymentOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public PaymentSucceededAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return PaymentEventType.PAYMENT_SUCCESS.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payloadJson) {
        PaymentSucceededPayload payload = objectMapper.readValue(payloadJson, PaymentSucceededPayload.class);
        
        // TODO
        return PaymentSucceededAvroEvent.newBuilder()
                .setOrderId(payload.getOrderId().toString())
                .build();
    }
}
