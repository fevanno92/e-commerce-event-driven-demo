package com.ecommerce.stock.infrastructure.repository.stock;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface StockJpaRepository extends JpaRepository<StockItemEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<StockItemEntity> findByProductId(UUID productId);
}
