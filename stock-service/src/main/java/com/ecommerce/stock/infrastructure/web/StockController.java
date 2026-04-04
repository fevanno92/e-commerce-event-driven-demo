package com.ecommerce.stock.infrastructure.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.stock.application.ports.input.StockApplicationService;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockApplicationService stockApplicationService;

    @Autowired
    public StockController(StockApplicationService stockApplicationService) {
        this.stockApplicationService = stockApplicationService;
    }

}
