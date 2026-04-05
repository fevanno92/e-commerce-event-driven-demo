package com.ecommerce.stock.infrastructure.messaging.producer.strategy;

import java.util.List;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockItem;
import com.ecommerce.common.avro.event.StockReservedAvroEvent;
import com.ecommerce.stock.application.outbox.payload.StockReservedPayload;
import com.ecommerce.stock.domain.event.StockEventType;

import tools.jackson.databind.ObjectMapper;

@Component
public class StockReservedAvroStrategy implements StockOutboxMessageStrategy {

    private final ObjectMapper objectMapper;

    public StockReservedAvroStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return StockEventType.STOCK_RESERVED.getValue().equals(eventType);
    }

    @Override
    public SpecificRecordBase mapToAvro(String payloadJson) {
        StockReservedPayload payload = objectMapper.readValue(payloadJson, StockReservedPayload.class);
        
        List<StockItem> items = payload.getItems().stream()
                .map(item -> StockItem.newBuilder()
                        .setProductId(item.getProductId().toString())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();

        return StockReservedAvroEvent.newBuilder()
                .setOrderId(payload.getOrderId().toString())
                .setCreatedAt(payload.getCreatedAt().toEpochMilli())
                .setItems(items)
                .build();
    }
}
