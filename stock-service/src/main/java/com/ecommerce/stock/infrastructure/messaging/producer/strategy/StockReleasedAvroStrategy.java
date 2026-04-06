package com.ecommerce.stock.infrastructure.messaging.producer.strategy;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockReleasedAvroEvent;
import com.ecommerce.common.event.payload.StockReleasedPayload;
import com.ecommerce.stock.domain.event.StockEventType;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class StockReleasedAvroStrategy implements StockOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public StockReleasedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return StockEventType.STOCK_RELEASED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payload) {
        try {
            StockReleasedPayload stockReleasedPayload = objectMapper.readValue(payload, StockReleasedPayload.class);
            
            return StockReleasedAvroEvent.newBuilder()
                    .setOrderId(stockReleasedPayload.orderId().toString())                    
                    .setCreatedAt(stockReleasedPayload.createdAt().toEpochMilli())
                    .build();
        } catch (Exception e) {
            log.error("Could not map payload to StockReleasedAvroEvent", e);
            throw new RuntimeException("Mapping error for StockReleasedAvroEvent", e);
        }
    }
}
