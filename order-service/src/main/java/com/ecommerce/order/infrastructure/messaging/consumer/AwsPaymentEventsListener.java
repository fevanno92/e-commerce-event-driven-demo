package com.ecommerce.order.infrastructure.messaging.consumer;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.order.application.dto.PaymentFailedCommand;
import com.ecommerce.order.application.dto.PaymentSucceededCommand;
import com.ecommerce.order.application.ports.input.PaymentMessageListener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("aws")
@Slf4j
public class AwsPaymentEventsListener {

    private final PaymentMessageListener paymentMessageListener;
    private final ObjectMapper objectMapper;

    public AwsPaymentEventsListener(PaymentMessageListener paymentMessageListener, ObjectMapper objectMapper) {
        this.paymentMessageListener = paymentMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("order-payment-queue")
    public void onMessage(String rawMessage) {
        log.info("Received raw SNS/SQS message: {}", rawMessage);
        try {
            // SNS messages in SQS are wrapped in a JSON structure by default
            JsonNode snsWrapper = objectMapper.readTree(rawMessage);
            String messageType = snsWrapper.path("Subject").asText();
            String innerMessage = snsWrapper.path("Message").asText();
            
            JsonNode event = objectMapper.readTree(innerMessage);
            
            if ("PaymentSucceededEvent".equals(messageType)) {
                log.info("Processing PaymentSucceeded event for order: {}", event.path("orderId").asText());
                paymentMessageListener.paymentSucceeded(new PaymentSucceededCommand(
                        event.path("orderId").asText(),
                        event.path("customerId").asText(),
                        event.path("amount").decimalValue()
                ));
            } else if ("PaymentFailedEvent".equals(messageType)) {
                log.warn("Processing PaymentFailed event for order: {}", event.path("orderId").asText());
                paymentMessageListener.paymentFailed(new PaymentFailedCommand(
                        event.path("orderId").asText(),
                        event.path("customerId").asText(),
                        event.path("reason").asText()
                ));
            } else {
                log.warn("Unknown message type: {}", messageType);
            }
        } catch (Exception e) {
            log.error("Error processing SQS message", e);
        }
    }
}
