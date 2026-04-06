package com.ecommerce.payment.infrastructure.messaging.consumer;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderValidatedPayload;
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

    private static final String ORDER_VALIDATED = "OrderValidatedEvent";

    private final OrderMessageListener orderMessageListener;
    private final ObjectMapper objectMapper;
    private final Map<String, Class<?>> eventMapping = Map.of(
            ORDER_VALIDATED, OrderValidatedPayload.class
    );

    public AwsOrderEventsListener(OrderMessageListener orderMessageListener, ObjectMapper objectMapper) {
        this.orderMessageListener = orderMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("payment-order-queue")
    public void onMessage(String rawJson) throws Exception {
        log.info("Received raw SNS/SQS message for payment service: {}", rawJson);
        JsonNode sns = objectMapper.readTree(rawJson);
        String subject = sns.get("Subject").asString();
        String messageBody = sns.get("Message").asString();
        
        Class<?> payloadClass = eventMapping.get(subject);
        if (payloadClass == null) {
            log.warn("Ignoring unknown or irrelevant order event type for payment: {}", subject);
            return;
        }

        Object payload = objectMapper.readValue(messageBody, payloadClass);
        handleEvent(subject, payload);
    }

    private void handleEvent(String subject, Object payload) {
        if (ORDER_VALIDATED.equals(subject)) {
            OrderValidatedPayload event = (OrderValidatedPayload) payload;
            log.info("Processing OrderValidated event for payment: {}", event.orderId());
            orderMessageListener.processPayment(new PaymentRequest(
                    event.orderId(),
                    event.customerId(),
                    event.totalAmount()
            ));
        }
    }
}
