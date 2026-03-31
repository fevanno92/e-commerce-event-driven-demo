package com.ecommerce.order.infrastructure.rest;

import java.math.BigDecimal;

public record ProductResponse(String id, String name, String description, BigDecimal price) {
}
