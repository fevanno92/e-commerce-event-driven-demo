package com.ecommerce.order.application.outbox.payload;

import java.time.Instant;
import java.util.List;

/**
 * DTO representing the JSON payload of an OrderCreated event stored in the outbox.
 * Used for serialization and deserialization, ensuring schema consistency.
 */
public record OrderCreatedPayload(
        String orderId,
        String customerId,
        String orderStatus,
        Instant createdAt,
        List<OrderItemPayload> items
) {
    public record OrderItemPayload(
            String productId,
            int quantity,
            double price
    ) {
    }
}
