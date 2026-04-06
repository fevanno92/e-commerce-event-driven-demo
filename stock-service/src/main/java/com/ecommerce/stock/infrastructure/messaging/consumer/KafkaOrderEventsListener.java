package com.ecommerce.stock.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderCreatedAvroEvent;
import com.ecommerce.common.avro.event.OrderPaidAvroEvent;
import com.ecommerce.common.avro.event.OrderPaymentFailedAvroEvent;
import com.ecommerce.stock.application.dto.ReserveStockRequest;
import com.ecommerce.stock.application.dto.ConfirmStockRequest;
import com.ecommerce.stock.application.dto.OrderItemDTO;
import com.ecommerce.stock.application.dto.ReleaseStockRequest;
import com.ecommerce.stock.application.ports.input.OrderMessageListener;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = "order-events", groupId = "stock-service")
public class KafkaOrderEventsListener {

    private final OrderMessageListener orderMessageListener;

    public KafkaOrderEventsListener(OrderMessageListener stockMessageListener) {
        this.orderMessageListener = stockMessageListener;
    }

    @KafkaHandler
    public void onOrderCreated(OrderCreatedAvroEvent orderCreatedEvent) {
        ReserveStockRequest request = new ReserveStockRequest(
                UUID.fromString(orderCreatedEvent.getOrderId()),
                orderCreatedEvent.getItems().stream()
                        .map(item -> new OrderItemDTO(
                                UUID.fromString(item.getProductId()),
                                item.getQuantity()))
                        .toList());
        orderMessageListener.reserveStock(request);
    }

    @KafkaHandler
    public void onOrderPaid(OrderPaidAvroEvent orderPaidEvent) {
        ConfirmStockRequest confirmStockRequest = new ConfirmStockRequest(UUID.fromString(orderPaidEvent.getOrderId()));
        orderMessageListener.confirmStock(confirmStockRequest);
    }

    @KafkaHandler
    public void onOrderPaymentFailed(OrderPaymentFailedAvroEvent orderPaymentFailedEvent) {
        ReleaseStockRequest releaseStockRequest = new ReleaseStockRequest(UUID.fromString(orderPaymentFailedEvent.getOrderId()));
        orderMessageListener.releaseStock(releaseStockRequest);
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
        log.warn("Ignoring order event with type: {}", object.getClass().getName());
    }
}
