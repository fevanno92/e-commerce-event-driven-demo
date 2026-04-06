package com.ecommerce.common.event.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record PaymentFailedPayload(
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String reason,
        Instant createdAt
) {
}
