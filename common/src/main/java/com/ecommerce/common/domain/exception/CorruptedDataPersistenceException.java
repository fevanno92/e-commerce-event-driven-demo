package com.ecommerce.common.domain.exception;

public class CorruptedDataPersistenceException extends RuntimeException {
    public CorruptedDataPersistenceException(String message) {
        super(message);
    }

    public CorruptedDataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
