package com.ecommerce.payment.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.payment.application.dto.PaymentRequest;
import com.ecommerce.payment.application.ports.input.OrderMessageListener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("aws")
@Slf4j
public class AwsOrderEventsListener {

    private final OrderMessageListener orderMessageListener;
    private final ObjectMapper objectMapper;

    public AwsOrderEventsListener(OrderMessageListener orderMessageListener, ObjectMapper objectMapper) {
        this.orderMessageListener = orderMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("payment-order-queue")
    public void onMessage(String rawMessage) {
        log.info("Received raw SNS/SQS message for payment service: {}", rawMessage);
        try {
            JsonNode snsWrapper = objectMapper.readTree(rawMessage);
            String messageType = snsWrapper.path("Subject").asText();
            String innerMessage = snsWrapper.path("Message").asText();
            
            JsonNode event = objectMapper.readTree(innerMessage);
            
            if ("OrderValidatedEvent".equals(messageType)) {
                log.info("Processing OrderValidated event for payment: {}", event.path("orderId").asText());
                orderMessageListener.processPayment(new PaymentRequest(
                        UUID.fromString(event.path("orderId").asText()),
                        UUID.fromString(event.path("customerId").asText()),
                        event.path("totalAmount").decimalValue()
                ));
            } else {
                log.warn("Ignoring unknown or irrelevant order event type: {}", messageType);
            }
        } catch (Exception e) {
            log.error("Error processing order SQS message in payment service", e);
        }
    }
}
