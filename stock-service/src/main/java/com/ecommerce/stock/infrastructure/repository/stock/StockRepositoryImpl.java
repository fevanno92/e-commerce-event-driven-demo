package com.ecommerce.stock.infrastructure.repository.stock;

import com.ecommerce.common.domain.exception.CorruptedDataPersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ecommerce.stock.application.ports.output.StockRepository;
import com.ecommerce.stock.domain.entity.StockItem;
import com.ecommerce.stock.domain.valueobject.ProductId;
import com.ecommerce.stock.domain.valueobject.StockItemId;

@Repository
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Autowired
    public StockRepositoryImpl(StockJpaRepository stockJpaRepository) {
        this.stockJpaRepository = stockJpaRepository;
    }

    @Override
    public StockItem save(StockItem stockItem) {
        StockItemEntity entity = toEntity(stockItem);
        StockItemEntity savedEntity = stockJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<StockItem> findAll() {
        return stockJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<StockItem> findByProductId(UUID productId) {
        return stockJpaRepository.findByProductId(productId)
                .map(this::toDomain);
    }

    private StockItem toDomain(StockItemEntity entity) {
        try {
            return new StockItem(new StockItemId(entity.getId()),
                    new ProductId(entity.getProductId()),
                    entity.getTotalQuantity(),
                    entity.getReservedQuantity());
        } catch (Exception e) {
            throw new CorruptedDataPersistenceException("Corrupted stock data found in database for ID: " + entity.getId(), e);
        }
    }

    private StockItemEntity toEntity(StockItem stockItem) {
        return StockItemEntity.builder()
                .id(stockItem.getId() != null ? stockItem.getId().getId() : UUID.randomUUID())
                .productId(stockItem.getProductId().getId())
                .totalQuantity(stockItem.getTotalQuantity())
                .reservedQuantity(stockItem.getReservedQuantity())
                .build();
    }

}
