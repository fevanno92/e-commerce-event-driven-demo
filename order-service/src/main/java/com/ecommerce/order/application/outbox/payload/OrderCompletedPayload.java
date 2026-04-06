package com.ecommerce.order.application.outbox.payload;

import java.time.Instant;

/**
 * Payload for the OrderCompleted event in the outbox.
 */
public record OrderCompletedPayload(
    String orderId,
    String customerId,  
    Instant createdAt
) {}
