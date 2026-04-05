package com.ecommerce.stock.infrastructure.repository.reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.ecommerce.common.domain.exception.CorruptedDataPersistenceException;
import com.ecommerce.stock.application.ports.output.StockReservationRepository;
import com.ecommerce.stock.domain.entity.StockReservation;
import com.ecommerce.stock.domain.entity.StockReservationItem;
import com.ecommerce.stock.domain.exception.InvalidStockReservationException;
import com.ecommerce.stock.domain.valueobject.OrderId;
import com.ecommerce.stock.domain.valueobject.ProductId;
import com.ecommerce.stock.domain.valueobject.StockReservationId;
import com.ecommerce.stock.domain.valueobject.StockReservationItemId;

@Repository
public class StockReservationRepositoryImpl implements StockReservationRepository {

    private final StockReservationJpaRepository stockReservationJpaRepository;

    public StockReservationRepositoryImpl(StockReservationJpaRepository stockReservationJpaRepository) {
        this.stockReservationJpaRepository = stockReservationJpaRepository;
    }

    @Override
    public StockReservation save(StockReservation stockReservation) {
        StockReservationEntity entity = toEntity(stockReservation);
        StockReservationEntity savedEntity = stockReservationJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<StockReservation> findByOrderId(UUID orderId) {
        return stockReservationJpaRepository.findByOrderId(orderId)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return stockReservationJpaRepository.existsByOrderId(orderId);
    }

    private StockReservation toDomain(StockReservationEntity entity) {
        try {                
        List<StockReservationItem> items = entity.getItems().stream()
                .map(itemEntity -> new StockReservationItem(
                        new StockReservationItemId(itemEntity.getId()),
                        new ProductId(itemEntity.getProductId()),
                        itemEntity.getQuantity()
                ))
                .toList();

        return new StockReservation(
                new StockReservationId(entity.getId()),
                new OrderId(entity.getOrderId()),
                items,
                entity.getStatus()
        );
        } catch (InvalidStockReservationException e) {
            throw new CorruptedDataPersistenceException("Corrupted stock reservation data found in database for ID: " + entity.getId(), e);
        }
    }

    private StockReservationEntity toEntity(StockReservation stockReservation) {
        StockReservationEntity entity = StockReservationEntity.builder()
                .id(stockReservation.getId().getValue())
                .orderId(stockReservation.getOrderId().getValue())
                .status(stockReservation.getStatus())
                .build();

        List<StockReservationItemEntity> items = stockReservation.getItems().stream()
                .map(item -> StockReservationItemEntity.builder()
                        .id(item.getId().getValue())
                        .reservation(entity)
                        .productId(item.getProductId().getValue())
                        .quantity(item.getQuantity())
                        .build())
                .toList();
        
        entity.setItems(items);
        return entity;
    }

}
