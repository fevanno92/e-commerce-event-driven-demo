package com.ecommerce.order.infrastructure.messaging.consumer;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ecommerce.common.event.payload.StockConfirmedPayload;
import com.ecommerce.common.event.payload.StockReleasedPayload;
import com.ecommerce.common.event.payload.StockReservedPayload;
import com.ecommerce.common.event.payload.StockUnavailablePayload;
import com.ecommerce.order.application.dto.CancelOrderCommand;
import com.ecommerce.order.application.dto.CompleteOrderCommand;
import com.ecommerce.order.application.dto.FailOrderCommand;
import com.ecommerce.order.application.dto.ValidateOrderCommand;
import com.ecommerce.order.application.ports.input.StockMessageListener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("aws")
@Slf4j
public class AwsStockEventsListener {

    private static final String STOCK_RESERVED = "StockReservedEvent";
    private static final String STOCK_UNAVAILABLE = "StockUnavailableEvent";
    private static final String STOCK_CONFIRMED = "StockConfirmedEvent";
    private static final String STOCK_RELEASED = "StockReleasedEvent";

    private final StockMessageListener stockMessageListener;
    private final ObjectMapper objectMapper;
    private final Map<String, Class<?>> eventMapping = Map.of(
            STOCK_RESERVED, StockReservedPayload.class,
            STOCK_UNAVAILABLE, StockUnavailablePayload.class,
            STOCK_CONFIRMED, StockConfirmedPayload.class,
            STOCK_RELEASED, StockReleasedPayload.class
    );

    public AwsStockEventsListener(StockMessageListener stockMessageListener, ObjectMapper objectMapper) {
        this.stockMessageListener = stockMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("order-stock-queue")
    public void onMessage(String rawJson) {
        log.info("Received raw SNS/SQS message for stock results: {}", rawJson);
        try {
            JsonNode sns = objectMapper.readTree(rawJson);
            String subject = sns.get("Subject").asString();
            String messageBody = sns.get("Message").asString();
            
            Class<?> payloadClass = eventMapping.get(subject);
            if (payloadClass == null) {
                log.warn("Ignoring unknown stock event type: {}", subject);
                return;
            }

            Object payload = objectMapper.readValue(messageBody, payloadClass);
            handleEvent(subject, payload);

        } catch (Exception e) {
            log.error("Error processing stock SQS message", e);
        }
    }

    private void handleEvent(String subject, Object payload) {
        switch (subject) {
            case STOCK_RESERVED -> {
                StockReservedPayload event = (StockReservedPayload) payload;
                log.info("Stock reserved for order: {}", event.orderId());
                stockMessageListener.validateOrder(new ValidateOrderCommand(event.orderId()));
            }
            case STOCK_UNAVAILABLE -> {
                StockUnavailablePayload event = (StockUnavailablePayload) payload;
                log.warn("Stock unavailable for order: {}", event.orderId());
                stockMessageListener.cancelOrder(new CancelOrderCommand(event.orderId(), event.reason()));
            }
            case STOCK_CONFIRMED -> {
                StockConfirmedPayload event = (StockConfirmedPayload) payload;
                log.info("Stock confirmed for order: {}", event.orderId());
                stockMessageListener.completeOrder(new CompleteOrderCommand(event.orderId()));
            }
            case STOCK_RELEASED -> {
                StockReleasedPayload event = (StockReleasedPayload) payload;
                log.error("Stock released for order: {}", event.orderId());
                stockMessageListener.failOrder(new FailOrderCommand(event.orderId()));
            }
        }
    }
}
