package com.ecommerce.stock.infrastructure.messaging.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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

    private final OrderMessageListener orderMessageListener;
    private final ObjectMapper objectMapper;

    public AwsOrderEventsListener(OrderMessageListener orderMessageListener, ObjectMapper objectMapper) {
        this.orderMessageListener = orderMessageListener;
        this.objectMapper = objectMapper;
    }

    @SqsListener("stock-order-queue")
    public void onMessage(String rawMessage) {
        log.info("Received raw SNS/SQS message for stock service: {}", rawMessage);
        try {
            JsonNode snsWrapper = objectMapper.readTree(rawMessage);
            String messageType = snsWrapper.path("Subject").asText();
            String innerMessage = snsWrapper.path("Message").asText();
            
            JsonNode event = objectMapper.readTree(innerMessage);
            
            UUID orderId = UUID.fromString(event.path("orderId").asText());

            switch (messageType) {
                case "OrderCreatedEvent":
                    log.info("Processing OrderCreated event for stock: {}", orderId);
                    List<OrderItemDTO> items = new ArrayList<>();
                    event.path("items").forEach(itemNode -> {
                        items.add(new OrderItemDTO(
                                UUID.fromString(itemNode.path("productId").asText()),
                                itemNode.path("quantity").asInt()
                        ));
                    });
                    orderMessageListener.reserveStock(new ReserveStockRequest(orderId, items));
                    break;
                case "OrderPaidEvent":
                    log.info("Processing OrderPaid event for stock: {}", orderId);
                    orderMessageListener.confirmStock(new ConfirmStockRequest(orderId));
                    break;
                case "OrderPaymentFailedEvent":
                    log.warn("Processing OrderPaymentFailed event for stock: {}", orderId);
                    orderMessageListener.releaseStock(new ReleaseStockRequest(orderId));
                    break;
                default:
                    log.warn("Ignoring unknown or irrelevant order event type: {}", messageType);
            }
        } catch (Exception e) {
            log.error("Error processing order SQS message in stock service", e);
        }
    }
}
