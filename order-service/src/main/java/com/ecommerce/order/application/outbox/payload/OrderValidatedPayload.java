package com.ecommerce.order.application.outbox.payload;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderValidatedPayload {
    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private long createdAt;
}
