package com.ecommerce.order.application.outbox.payload;

import java.time.Instant;

/**
 * Payload for the OrderFailed event in the outbox.
 */
public record OrderFailedPayload(
    String orderId,
    String customerId,  
    Instant createdAt
) {}
