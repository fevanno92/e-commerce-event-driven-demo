package com.ecommerce.product.dto;

public record ProductDTO(
        String id,
        String name,
        String description,
        double price) {
}
