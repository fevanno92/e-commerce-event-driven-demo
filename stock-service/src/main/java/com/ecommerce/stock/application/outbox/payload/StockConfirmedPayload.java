package com.ecommerce.stock.application.outbox.payload;

import java.time.Instant;

/**
 * Payload for the StockConfirmed event in the outbox.
 */
public record StockConfirmedPayload(
    String orderId,
    Instant createdAt
) {}
