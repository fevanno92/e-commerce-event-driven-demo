package com.ecommerce.stock.infrastructure.repository.stock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockJpaRepository extends JpaRepository<StockItemEntity, UUID> {
    Optional<StockItemEntity> findByProductId(UUID productId);
}
