package com.ecommerce.stock.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockUnavailableAvroEvent;
import com.ecommerce.common.event.payload.StockUnavailablePayload;
import com.ecommerce.stock.domain.event.StockEventType;

import tools.jackson.databind.ObjectMapper;

@Component
public class StockUnavailableAvroStrategy implements StockOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public StockUnavailableAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return StockEventType.STOCK_UNAVAILABLE.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payloadJson) {
        try {
            StockUnavailablePayload payload = objectMapper.readValue(payloadJson, StockUnavailablePayload.class);

            return StockUnavailableAvroEvent.newBuilder()
                    .setOrderId(payload.orderId().toString())
                    .setCreatedAt(payload.createdAt().toEpochMilli())
                    .setReason(payload.reason())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to map outbox message to Avro in StockUnavailableAvroStrategy", e);
        }
    }
}
