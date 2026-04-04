package com.ecommerce.stock.domain.exception;

public class InvalidStockItemException extends RuntimeException {
    public InvalidStockItemException(String message) {
        super(message);
    }
}
