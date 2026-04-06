package com.ecommerce.order.application.outbox.payload;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payload for the OrderPaid event in the outbox.
 */
public record OrderPaidPayload(
    String orderId,
    String customerId,
    BigDecimal totalAmount,
    Instant createdAt
) {}
