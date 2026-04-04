package com.ecommerce.stock.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCreatedAvroEvent;
import com.ecommerce.stock.application.dto.ReserveStockRequest;
import com.ecommerce.stock.application.dto.StockItemDTO;
import com.ecommerce.stock.application.ports.input.StockMessageListener;

@Component
public class KafkaMessageListener {
    
    private final StockMessageListener stockMessageListener;

    public KafkaMessageListener(StockMessageListener stockMessageListener) {
        this.stockMessageListener = stockMessageListener;
    }

    @KafkaListener(topics = "order-events", groupId = "stock-service")
    public void onOrderCreated(OrderCreatedAvroEvent orderCreatedEvent) {
        ReserveStockRequest request = new ReserveStockRequest(
            UUID.fromString(orderCreatedEvent.getOrderId()),
            orderCreatedEvent.getItems().stream()
                .map(item -> new StockItemDTO(
                    UUID.fromString(item.getProductId()),
                    item.getQuantity()
                ))
                .toList()
        );
        stockMessageListener.reserveStock(request);
    }
}
