package com.ecommerce.order.application.outbox.payload;

import java.time.Instant;

/**
 * Payload for the OrderPaymentFailed event in the outbox.
 */
public record OrderPaymentFailedPayload(
    String orderId,
    String customerId,
    String reason,
    Instant createdAt
) {}
