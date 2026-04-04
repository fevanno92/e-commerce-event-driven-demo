package com.ecommerce.order.infrastructure.rest;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.ecommerce.order.application.ports.output.ProductClient;
import com.ecommerce.order.domain.entity.Product;
import com.ecommerce.order.domain.exception.InvalidProductException;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.ProductId;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProductClientImpl implements ProductClient {

    private static final String PRODUCT_SERVICE = "productService";
    
    private final RestClient restClient;

    public ProductClientImpl(RestClient.Builder restClientBuilder,
            @Value("${app.product-service.url}") String productServiceBaseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(productServiceBaseUrl)
                .build();
    }

    @Override
    @CircuitBreaker(name = PRODUCT_SERVICE, fallbackMethod = "getProductByIdFallback")
    public Optional<Product> getProductById(UUID productId) {
        try {
            ProductResponse response = restClient.get()
                    .uri("/products/{productId}", productId.toString())
                    .retrieve()
                    .body(ProductResponse.class);

            log.info("Received product response: {}", response);
            
            try {
                return Optional.of(new Product(
                    new ProductId(productId), 
                    response.name(), 
                    response.description(), 
                    new Money(response.price())));
            } catch (InvalidProductException ex) {
                log.error("Product returned from product service is invalid for ID {}: {}", productId, ex.getMessage());
                return Optional.empty();
            }

        } catch (RestClientResponseException ex) {
            log.error("Error fetching product with ID {}: {}", productId, ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    /**
     * Fallback method called when circuit breaker is OPEN or an exception occurs.
     * Must have the same return type and parameters as the original method, plus Throwable.
     */
    private Optional<Product> getProductByIdFallback(UUID productId, Throwable throwable) {
        log.warn("Circuit breaker triggered for product ID {}. Reason: {}", productId, throwable.getMessage());
        // Return empty or throw a custom exception depending on your business logic
        return Optional.empty();
    }
}
