package com.ecommerce.order.infrastructure.messaging.consumer;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.common.avro.event.PaymentFailedAvroEvent;
import com.ecommerce.common.avro.event.PaymentSucceededAvroEvent;
import com.ecommerce.order.application.dto.PaymentFailedCommand;
import com.ecommerce.order.application.dto.PaymentSucceededCommand;
import com.ecommerce.order.application.ports.input.PaymentMessageListener;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@KafkaListener(topics = "payment-events", groupId = "order-service")
public class KafkaPaymentEventsListener {

    private final PaymentMessageListener paymentMessageListener;

    public KafkaPaymentEventsListener(PaymentMessageListener paymentMessageListener) {
        this.paymentMessageListener = paymentMessageListener;
    }

    @KafkaHandler
    public void onPaymentSucceeded(PaymentSucceededAvroEvent event) {
        log.info("Received PaymentSucceededAvroEvent for order: {}", event.getOrderId());
        paymentMessageListener.paymentSucceeded(new PaymentSucceededCommand(
                event.getOrderId(),
                event.getCustomerId(),
                event.getAmount()
        ));
    }

    @KafkaHandler
    public void onPaymentFailed(PaymentFailedAvroEvent event) {
        log.info("Received PaymentFailedAvroEvent for order: {}", event.getOrderId());
        paymentMessageListener.paymentFailed(new PaymentFailedCommand(
                event.getOrderId(),
                event.getCustomerId(),
                event.getReason()
        ));
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
        log.warn("Ignoring payment event with type: {}", object.getClass().getName());
    }
}
