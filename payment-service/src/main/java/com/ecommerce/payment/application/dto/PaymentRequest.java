package com.ecommerce.payment.application.dto;

import java.util.UUID;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
    @NotNull UUID orderId,
    @Positive BigDecimal amount
) {
}
