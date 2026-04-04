package com.ecommerce.order.infrastructure.messaging.consumer;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.StockReservedAvroEvent;
import com.ecommerce.common.avro.event.StockUnavailableAvroEvent;
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
        log.info("Received StockReservedAvroEvent: {}", stockReservedEvent);  
    }

    @KafkaHandler
    public void onStockUnavailable(StockUnavailableAvroEvent stockUnavailableEvent) {
        log.info("Received StockUnavailableAvroEvent: {}", stockUnavailableEvent);  
    }
}
