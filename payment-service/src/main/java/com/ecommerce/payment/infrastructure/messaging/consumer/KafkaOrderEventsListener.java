package com.ecommerce.payment.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.OrderValidatedAvroEvent;
import com.ecommerce.payment.application.dto.PaymentRequest;
import com.ecommerce.payment.application.ports.input.OrderMessageListener;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("kafka")
@Slf4j
@KafkaListener(topics = "order-events", groupId = "payment-service")
public class KafkaOrderEventsListener {

    private final OrderMessageListener orderMessageListener;

    public KafkaOrderEventsListener(OrderMessageListener orderMessageListener) {
        this.orderMessageListener = orderMessageListener;
    }

    @KafkaHandler
    public void onOrderValidated(OrderValidatedAvroEvent orderValidatedEvent) {
        orderMessageListener.processPayment(new PaymentRequest(
            UUID.fromString(orderValidatedEvent.getOrderId()),
            UUID.fromString(orderValidatedEvent.getCustomerId()),
            orderValidatedEvent.getTotalAmount()
        ));
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
        log.warn("Ignoring order event with type: {}", object.getClass().getName());
    }
}
