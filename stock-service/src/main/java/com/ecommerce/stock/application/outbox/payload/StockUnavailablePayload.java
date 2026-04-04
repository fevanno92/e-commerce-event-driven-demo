package com.ecommerce.stock.application.outbox.payload;

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
public class StockUnavailablePayload {
    @JsonProperty
    private UUID orderId;
    @JsonProperty
    private String reason;
    @JsonProperty
    private Instant createdAt;
}
