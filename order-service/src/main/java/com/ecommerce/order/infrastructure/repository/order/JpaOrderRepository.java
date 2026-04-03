package com.ecommerce.order.infrastructure.repository.order;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
}