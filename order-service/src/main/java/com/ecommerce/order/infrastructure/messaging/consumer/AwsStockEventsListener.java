package com.ecommerce.order.infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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

    private final StockMessageListener stockMessageListener;
    private final ObjectMapper objectMapper;

    public AwsStockEventsListener(StockMessageListener stockMessageListener, ObjectMapper objectMapper) {
        this.stockMessageListener = stockMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("order-stock-queue")
    public void onMessage(String rawMessage) {
        log.info("Received raw SNS/SQS message for stock: {}", rawMessage);
        try {
            JsonNode snsWrapper = objectMapper.readTree(rawMessage);
            String messageType = snsWrapper.path("Subject").asText();
            String innerMessage = snsWrapper.path("Message").asText();
            
            JsonNode event = objectMapper.readTree(innerMessage);
            
            UUID orderId = UUID.fromString(event.path("orderId").asText());

            switch (messageType) {
                case "StockReservedEvent":
                    log.info("Stock reserved for order: {}", orderId);
                    stockMessageListener.validateOrder(new ValidateOrderCommand(orderId));
                    break;
                case "StockUnavailableEvent":
                    log.warn("Stock unavailable for order: {}", orderId);
                    stockMessageListener.cancelOrder(new CancelOrderCommand(orderId, event.path("reason").asText()));
                    break;
                case "StockConfirmedEvent":
                    log.info("Stock confirmed for order: {}", orderId);
                    stockMessageListener.completeOrder(new CompleteOrderCommand(orderId));
                    break;
                case "StockReleasedEvent":
                    log.error("Stock released for order: {}", orderId);
                    stockMessageListener.failOrder(new FailOrderCommand(orderId));
                    break;
                default:
                    log.warn("Unknown stock event type: {}", messageType);
            }
        } catch (Exception e) {
            log.error("Error processing stock SQS message", e);
        }
    }
}
