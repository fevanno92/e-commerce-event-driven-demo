package com.ecommerce.stock.application.outbox.payload;

import java.time.Instant;

/**
 * Payload for the StockReleased event in the outbox.
 */
public record StockReleasedPayload(
    String orderId,    
    Instant createdAt
) {}
