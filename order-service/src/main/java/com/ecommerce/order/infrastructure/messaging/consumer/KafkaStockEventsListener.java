package com.ecommerce.order.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockConfirmedAvroEvent;
import com.ecommerce.common.avro.event.StockReleasedAvroEvent;
import com.ecommerce.common.avro.event.StockReservedAvroEvent;
import com.ecommerce.common.avro.event.StockUnavailableAvroEvent;
import com.ecommerce.order.application.dto.CancelOrderCommand;
import com.ecommerce.order.application.dto.CompleteOrderCommand;
import com.ecommerce.order.application.dto.FailOrderCommand;
import com.ecommerce.order.application.dto.ValidateOrderCommand;
import com.ecommerce.order.application.ports.input.StockMessageListener;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = "stock-events", groupId = "order-service")
public class KafkaStockEventsListener {

    private final StockMessageListener stockMessageListener;

    public KafkaStockEventsListener(StockMessageListener stockMessageListener) {
        this.stockMessageListener = stockMessageListener;
    }

    @KafkaHandler
    public void onStockReserved(StockReservedAvroEvent stockReservedEvent) {        
        UUID orderId = UUID.fromString(stockReservedEvent.getOrderId());
        stockMessageListener.validateOrder(new ValidateOrderCommand(orderId));
    }

    @KafkaHandler
    public void onStockUnavailable(StockUnavailableAvroEvent stockUnavailableEvent) {
        UUID orderId = UUID.fromString(stockUnavailableEvent.getOrderId());
        stockMessageListener.cancelOrder(new CancelOrderCommand(orderId, stockUnavailableEvent.getReason()));
    }

    @KafkaHandler
    public void onStockConfirmed(StockConfirmedAvroEvent stockConfirmedEvent) {
        UUID orderId = UUID.fromString(stockConfirmedEvent.getOrderId());
        stockMessageListener.completeOrder(new CompleteOrderCommand(orderId));
    }

    @KafkaHandler
    public void onStockReleased(StockReleasedAvroEvent stockReleasedEvent) {
        UUID orderId = UUID.fromString(stockReleasedEvent.getOrderId());
        stockMessageListener.failOrder(new FailOrderCommand(orderId));
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
        log.warn("Ignoring stock event with type: {}", object.getClass().getName());
    }
}
