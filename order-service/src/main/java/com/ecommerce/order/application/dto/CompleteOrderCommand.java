package com.ecommerce.order.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CompleteOrderCommand(
        @NotNull UUID orderId) {

}
