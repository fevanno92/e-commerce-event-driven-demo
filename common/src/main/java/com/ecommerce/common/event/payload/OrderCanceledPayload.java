package com.ecommerce.common.event.payload;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record OrderCanceledPayload(
        UUID orderId,
        String reason,
        Instant createdAt
) {
}
