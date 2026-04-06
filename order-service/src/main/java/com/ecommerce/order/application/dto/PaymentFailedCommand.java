package com.ecommerce.order.application.dto;

public record PaymentFailedCommand(
    String orderId,
    String customerId,
    String reason
) {}
