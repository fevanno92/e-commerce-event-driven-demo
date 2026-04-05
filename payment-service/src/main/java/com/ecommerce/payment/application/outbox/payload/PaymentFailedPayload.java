package com.ecommerce.payment.application.outbox.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedPayload {
    @JsonProperty
    private UUID orderId;    
    @JsonProperty
    private UUID customerId;
    @JsonProperty
    private BigDecimal amount;
    @JsonProperty
    private String reason;
    @JsonProperty
    private Instant createdAt;
}
