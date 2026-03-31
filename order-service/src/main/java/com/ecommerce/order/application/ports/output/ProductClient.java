package com.ecommerce.order.application.ports.output;

import java.util.Optional;
import java.util.UUID;

import com.ecommerce.order.domain.entity.Product;

public interface ProductClient {
    Optional<Product> getProductById(UUID productId);
}
