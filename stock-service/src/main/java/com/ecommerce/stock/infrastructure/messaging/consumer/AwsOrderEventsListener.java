package com.ecommerce.stock.infrastructure.messaging.consumer;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.OrderCreatedPayload;
import com.ecommerce.common.event.payload.OrderPaidPayload;
import com.ecommerce.common.event.payload.OrderPaymentFailedPayload;
import com.ecommerce.stock.application.dto.ConfirmStockRequest;
import com.ecommerce.stock.application.dto.OrderItemDTO;
import com.ecommerce.stock.application.dto.ReleaseStockRequest;
import com.ecommerce.stock.application.dto.ReserveStockRequest;
import com.ecommerce.stock.application.ports.input.OrderMessageListener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("aws")
@Slf4j
public class AwsOrderEventsListener {

    private static final String ORDER_CREATED = "OrderCreatedEvent";
    private static final String ORDER_PAID = "OrderPaidEvent";
    private static final String ORDER_PAYMENT_FAILED = "OrderPaymentFailedEvent";

    private final OrderMessageListener orderMessageListener;
    private final ObjectMapper objectMapper;
    private final Map<String, Class<?>> eventMapping = Map.of(
            ORDER_CREATED, OrderCreatedPayload.class,
            ORDER_PAID, OrderPaidPayload.class,
            ORDER_PAYMENT_FAILED, OrderPaymentFailedPayload.class
    );

    public AwsOrderEventsListener(OrderMessageListener orderMessageListener, ObjectMapper objectMapper) {
        this.orderMessageListener = orderMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("stock-order-queue")
    public void onMessage(String rawJson) {
        log.info("Received raw SNS/SQS message for stock service: {}", rawJson);
        try {
            JsonNode sns = objectMapper.readTree(rawJson);
            String subject = sns.get("Subject").asString();
            String messageBody = sns.get("Message").asString();
            
            Class<?> payloadClass = eventMapping.get(subject);
            if (payloadClass == null) {
                log.warn("Ignoring unknown or irrelevant order event type for stock: {}", subject);
                return;
            }

            Object payload = objectMapper.readValue(messageBody, payloadClass);
            handleEvent(subject, payload);

        } catch (Exception e) {
            log.error("Error processing order SQS message in stock service", e);
        }
    }

    private void handleEvent(String subject, Object payload) {
        switch (subject) {
            case ORDER_CREATED -> {
                OrderCreatedPayload event = (OrderCreatedPayload) payload;
                log.info("Processing OrderCreated event for stock: {}", event.orderId());
                orderMessageListener.reserveStock(new ReserveStockRequest(
                        event.orderId(),
                        event.items().stream()
                                .map(item -> new OrderItemDTO(item.productId(), item.quantity()))
                                .collect(Collectors.toList())
                ));
            }
            case ORDER_PAID -> {
                OrderPaidPayload event = (OrderPaidPayload) payload;
                log.info("Processing OrderPaid event for stock: {}", event.orderId());
                orderMessageListener.confirmStock(new ConfirmStockRequest(event.orderId()));
            }
            case ORDER_PAYMENT_FAILED -> {
                OrderPaymentFailedPayload event = (OrderPaymentFailedPayload) payload;
                log.warn("Processing OrderPaymentFailed event for stock: {}", event.orderId());
                orderMessageListener.releaseStock(new ReleaseStockRequest(event.orderId()));
            }
        }
    }
}
