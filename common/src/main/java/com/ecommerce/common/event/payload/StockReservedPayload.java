package com.ecommerce.common.event.payload;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record StockReservedPayload(
        UUID orderId,
        List<StockItemPayload> items,
        Instant createdAt
) {
    @Builder
    public record StockItemPayload(
            UUID productId,
            int quantity
    ) {
    }
}
