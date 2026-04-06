package com.ecommerce.order.application.dto;

import java.math.BigDecimal;

public record PaymentSucceededCommand(
    String orderId,
    String customerId,
    BigDecimal amount
) {}
