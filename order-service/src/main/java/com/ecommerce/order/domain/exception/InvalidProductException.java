package com.ecommerce.order.domain.exception;

public class InvalidProductException extends RuntimeException {
    public InvalidProductException(String message) {
        super(message);
    }    
}
