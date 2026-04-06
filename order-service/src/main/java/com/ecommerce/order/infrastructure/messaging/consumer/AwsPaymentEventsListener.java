package com.ecommerce.order.infrastructure.messaging.consumer;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.PaymentFailedPayload;
import com.ecommerce.common.event.payload.PaymentSucceededPayload;
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

    private static final String PAYMENT_SUCCEEDED = "PaymentSucceededEvent";
    private static final String PAYMENT_FAILED = "PaymentFailedEvent";

    private final PaymentMessageListener paymentMessageListener;
    private final ObjectMapper objectMapper;
    private final Map<String, Class<?>> eventMapping = Map.of(
            PAYMENT_SUCCEEDED, PaymentSucceededPayload.class,
            PAYMENT_FAILED, PaymentFailedPayload.class
    );

    public AwsPaymentEventsListener(PaymentMessageListener paymentMessageListener, ObjectMapper objectMapper) {
        this.paymentMessageListener = paymentMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("order-payment-queue")
    public void onMessage(String rawJson) throws Exception {
        log.info("Received raw SNS/SQS message for payment results: {}", rawJson);
        JsonNode sns = objectMapper.readTree(rawJson);
        String subject = sns.get("Subject").asString();
        String messageBody = sns.get("Message").asString();
        
        Class<?> payloadClass = eventMapping.get(subject);
        if (payloadClass == null) {
            log.warn("Ignoring unknown payment event type: {}", subject);
            return;
        }

        Object payload = objectMapper.readValue(messageBody, payloadClass);
        handleEvent(subject, payload);
    }

    private void handleEvent(String subject, Object payload) {
        switch (subject) {
            case PAYMENT_SUCCEEDED -> {
                PaymentSucceededPayload event = (PaymentSucceededPayload) payload;
                log.info("Processing PaymentSucceeded for order: {}", event.orderId());
                paymentMessageListener.paymentSucceeded(new PaymentSucceededCommand(
                        event.orderId().toString(),
                        event.customerId().toString(),
                        event.amount()
                ));
            }
            case PAYMENT_FAILED -> {
                PaymentFailedPayload event = (PaymentFailedPayload) payload;
                log.warn("Processing PaymentFailed for order: {}", event.orderId());
                paymentMessageListener.paymentFailed(new PaymentFailedCommand(
                        event.orderId().toString(),
                        event.customerId().toString(),
                        event.reason()
                ));
            }
        }
    }
}
