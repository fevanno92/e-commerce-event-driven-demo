package com.ecommerce.order.application.outbox.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCanceledPayload {
    private String orderId;
    private String reason;
    private long createdAt;
}
