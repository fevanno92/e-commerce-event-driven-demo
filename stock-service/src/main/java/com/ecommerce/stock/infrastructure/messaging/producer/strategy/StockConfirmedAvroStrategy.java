package com.ecommerce.stock.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockConfirmedAvroEvent;
import com.ecommerce.common.event.payload.StockConfirmedPayload;
import com.ecommerce.stock.domain.event.StockEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class StockConfirmedAvroStrategy implements StockOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public StockConfirmedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return StockEventType.STOCK_CONFIRMED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            StockConfirmedPayload stockConfirmedPayload = objectMapper.readValue(payload, StockConfirmedPayload.class);
            
            return StockConfirmedAvroEvent.newBuilder()
                    .setOrderId(stockConfirmedPayload.orderId().toString())
                    .setCreatedAt(stockConfirmedPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to StockConfirmedAvroEvent", e);
            throw new RuntimeException("Mapping error for StockConfirmedAvroEvent", e);
        }
    }
}
