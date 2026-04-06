package com.ecommerce.common.event.payload;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record StockReleasedPayload(
        UUID orderId,
        Instant createdAt
) {
}
