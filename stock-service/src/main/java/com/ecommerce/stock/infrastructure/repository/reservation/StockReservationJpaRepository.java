package com.ecommerce.stock.infrastructure.repository.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockReservationJpaRepository extends JpaRepository<StockReservationEntity, UUID> {
    Optional<StockReservationEntity> findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}
