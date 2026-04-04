package com.ecommerce.stock.application;

import org.springframework.stereotype.Service;

import com.ecommerce.stock.application.dto.ReserveStockRequest;
import com.ecommerce.stock.application.ports.input.StockMessageListener;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockMessageListenerImpl implements StockMessageListener {

    @Override
    public void reserveStock(ReserveStockRequest request) {
        log.info("Received reserve stock request: {}", request);
    }
}
