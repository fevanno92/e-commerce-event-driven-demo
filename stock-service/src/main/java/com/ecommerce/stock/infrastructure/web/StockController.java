package com.ecommerce.stock.infrastructure.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stock.application.dto.AddStockRequest;
import com.ecommerce.stock.application.dto.StockItemDTO;
import com.ecommerce.stock.application.ports.input.StockApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockApplicationService stockApplicationService;

    @Autowired
    public StockController(StockApplicationService stockApplicationService) {
        this.stockApplicationService = stockApplicationService;
    }

    @GetMapping
    public List<StockItemDTO> getAllStockItems() {
        return stockApplicationService.getAllStockItems();
    }   

    @PostMapping
    public StockItemDTO addStock(@Valid @RequestBody AddStockRequest request) {
        return stockApplicationService.addStock(request);
    }

}
