package com.ecommerce.stock.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockUnavailableAvroEvent;
import com.ecommerce.stock.application.outbox.payload.StockUnavailablePayload;
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
        StockUnavailablePayload payload = objectMapper.readValue(payloadJson, StockUnavailablePayload.class);

        return StockUnavailableAvroEvent.newBuilder()
                .setOrderId(payload.getOrderId().toString())
                .setCreatedAt(payload.getCreatedAt().toEpochMilli())
                .setReason(payload.getReason())
                .build();
    }
}
