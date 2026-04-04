package com.ecommerce.stock.application.ports.output;

import java.util.Optional;
import java.util.UUID;

import com.ecommerce.stock.domain.entity.StockReservation;

public interface StockReservationRepository {
    StockReservation save(StockReservation stockReservation);
    Optional<StockReservation> findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}
