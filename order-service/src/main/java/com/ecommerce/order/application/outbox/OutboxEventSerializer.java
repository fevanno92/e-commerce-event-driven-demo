package com.ecommerce.order.application.outbox;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.order.application.outbox.payload.OrderCreatedPayload;
import com.ecommerce.order.application.outbox.payload.OrderCreatedPayload.OrderItemPayload;
import com.ecommerce.order.domain.event.OrderCreatedEvent;

import tools.jackson.databind.ObjectMapper;

/**
 * Serializes domain events to JSON for storage in the outbox.
 */
@Component
public class OutboxEventSerializer {

    private final ObjectMapper objectMapper;

    @Autowired
    public OutboxEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Create an OutboxMessage from an OrderCreatedEvent.
     */
    public OutboxMessage createOutboxMessage(OrderCreatedEvent event) {
        String payload = serializeOrderCreatedEvent(event);
        return OutboxMessage.create(
                "Order",
                event.getOrder().getId().getId().toString(),
                OutboxEventType.ORDER_CREATED,
                payload
        );
    }

    private String serializeOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            List<OrderItemPayload> items = event.getOrder().getItems().stream()
                    .map(item -> new OrderItemPayload(
                            item.getProductId().getId().toString(),
                            item.getQuantity(),
                            item.getPrice().getAmount().doubleValue()))
                    .toList();

            OrderCreatedPayload payload = new OrderCreatedPayload(
                    event.getOrder().getId().getId().toString(),
                    event.getOrder().getCustomerId().getId().toString(),
                    event.getOrder().getStatus().toString(),
                    event.getOrder().getCreatedAt().toEpochMilli(),
                    items);

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize OrderCreatedEvent to JSON", e);
        }
    }
}
