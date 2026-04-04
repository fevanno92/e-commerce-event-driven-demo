package com.ecommerce.stock;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ecommerce.stock.infrastructure.repository.stock.StockItemEntity;
import com.ecommerce.stock.infrastructure.repository.stock.StockJpaRepository;

@Component
public class DefaultStockItems implements CommandLineRunner {

    private final StockJpaRepository stockRepository;

    @Autowired
    public DefaultStockItems(StockJpaRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (stockRepository.count() == 0) {
            stockRepository.save(new StockItemEntity(UUID.randomUUID(), UUID.fromString("33b525be-a713-4afb-a111-785d603b02e4"), 10, 0));
            stockRepository.save(new StockItemEntity(UUID.randomUUID(), UUID.fromString("1c9f898f-4718-44c7-8ba7-dcadee99e2f0"), 10, 0));
            stockRepository.save(new StockItemEntity(UUID.randomUUID(), UUID.fromString("a64b7c22-7dbf-4e0c-800f-8a48d93f17c5"), 10, 0));
        }
    }

}
