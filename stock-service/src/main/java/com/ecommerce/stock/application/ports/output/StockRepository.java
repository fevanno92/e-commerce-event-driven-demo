package com.ecommerce.stock.application.ports.output;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ecommerce.stock.domain.entity.StockItem;

public interface StockRepository {
    StockItem save(StockItem stockItem);

    List<StockItem> findAll();

    Optional<StockItem> findByProductIdWithLock(UUID productId);
}
