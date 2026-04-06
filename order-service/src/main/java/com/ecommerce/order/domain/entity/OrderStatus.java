package com.ecommerce.order.domain.entity;

public enum OrderStatus {
    PENDING, // Order has been created but not yet processed
    RESERVED, // Inventory has been reserved for the order
    PAID, // Payment has been successful, waiting for final stock confirmation
    PAYMENT_FAILED, // Payment failed, waiting for stock reservation release
    COMPLETED, // Order has been completed successfully
    FAILED, // Order processing failed (payment failure)
    CANCELLED // Order has been cancelled because inventory reservation failed
}
