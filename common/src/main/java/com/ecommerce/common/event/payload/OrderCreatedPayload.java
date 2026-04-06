package com.ecommerce.common.event.payload;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record OrderCreatedPayload(
        UUID orderId,
        UUID customerId,
        String orderStatus,
        Instant createdAt,
        List<OrderItemPayload> items
) {
    @Builder
    public record OrderItemPayload(
            UUID productId,
            int quantity,
            double price
    ) {
    }
}
