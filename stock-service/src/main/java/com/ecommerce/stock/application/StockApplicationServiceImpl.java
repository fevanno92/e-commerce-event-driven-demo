package com.ecommerce.stock.application;

import com.ecommerce.stock.domain.valueobject.StockItemId;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.stock.application.dto.AddStockRequest;
import com.ecommerce.stock.application.dto.StockItemDTO;
import com.ecommerce.stock.application.ports.input.StockApplicationService;
import com.ecommerce.stock.application.ports.output.StockRepository;
import com.ecommerce.stock.domain.entity.StockItem;
import com.ecommerce.stock.domain.valueobject.ProductId;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockApplicationServiceImpl implements StockApplicationService {

    private final StockRepository stockRepository;

    @Autowired
    public StockApplicationServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public StockItemDTO addStock(AddStockRequest request) {
        StockItem savedStockItem = stockRepository.findByProductIdWithLock(request.productId())
                .map(stockItem -> {
                    stockItem.addQuantity(request.quantity());
                    return stockRepository.save(stockItem);
                })
                .orElseGet(() -> {
                    StockItem stockItem = new StockItem(new ProductId(request.productId()));
                    stockItem.addQuantity(request.quantity());
                    return stockRepository.save(stockItem);
                });

        return new StockItemDTO(savedStockItem.getId().getId(),
                savedStockItem.getProductId().getId(),
                savedStockItem.getTotalQuantity(),
                savedStockItem.getReservedQuantity());
    }

    @Override
    @Transactional
    public List<StockItemDTO> getAllStockItems() {
        return stockRepository.findAll().stream().map(stockItem -> new StockItemDTO(stockItem.getId().getId(),
                stockItem.getProductId().getId(),
                stockItem.getTotalQuantity(),
                stockItem.getReservedQuantity())).toList();
    }
}