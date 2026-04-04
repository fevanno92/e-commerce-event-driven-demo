package com.ecommerce.stock.domain.exception;

public class InvalidStockReservationException extends RuntimeException {
    public InvalidStockReservationException(String message) {
        super(message);
    }
    
}
