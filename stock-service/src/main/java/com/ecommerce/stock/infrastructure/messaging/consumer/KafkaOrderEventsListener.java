package com.ecommerce.stock.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCreatedAvroEvent;
import com.ecommerce.stock.application.dto.ReserveStockRequest;
import com.ecommerce.stock.application.dto.OrderItemDTO;
import com.ecommerce.stock.application.ports.input.OrderMessageListener;

@Component
public class KafkaOrderEventsListener {

    private final OrderMessageListener stockMessageListener;

    public KafkaOrderEventsListener(OrderMessageListener stockMessageListener) {
        this.stockMessageListener = stockMessageListener;
    }

    @KafkaListener(topics = "order-events", groupId = "stock-service")
    public void onOrderCreated(OrderCreatedAvroEvent orderCreatedEvent) {
        ReserveStockRequest request = new ReserveStockRequest(
                UUID.fromString(orderCreatedEvent.getOrderId()),
                orderCreatedEvent.getItems().stream()
                        .map(item -> new OrderItemDTO(
                                UUID.fromString(item.getProductId()),
                                item.getQuantity()))
                        .toList());
        stockMessageListener.reserveStock(request);
    }
}
