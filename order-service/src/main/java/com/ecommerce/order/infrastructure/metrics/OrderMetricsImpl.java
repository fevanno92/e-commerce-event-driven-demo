package com.ecommerce.order.infrastructure.metrics;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ecommerce.order.application.ports.output.OrderMetrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderMetricsImpl implements OrderMetrics {

    private final MeterRegistry meterRegistry;

    public OrderMetricsImpl(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Record successful order creation
     */
    public void recordOrderCreationSuccess() {
        meterRegistry.counter("order.create", "status", "success").increment();
        log.debug("Recorded successful order creation metric");
    }

    /**
     * Record failed order creation with error reason
     */
    public void recordOrderCreationFailure(String errorReason) {
        meterRegistry.counter("order.create", "status", "failure", "error", errorReason).increment();
        log.debug("Recorded failed order creation metric: {}", errorReason);
    }

    /**
     * Record product service call timing and status
     */
    public void recordProductServiceCall(UUID productId, long durationMs, boolean success) {
        // Record timer - measures the duration of product service calls
        meterRegistry.timer("product.service.call.duration", "status", success ? "success" : "failure")
                .record(Duration.ofMillis(durationMs));

        // Record counter - tracks total number of calls
        meterRegistry.counter("product.service.call", "product_id", productId.toString(), "status",
                success ? "success" : "failure").increment();

        log.debug("Recorded product service call: productId={}, duration={}ms, success={}", productId, durationMs,
                success);
    }

    /**
     * Record product not found error
     */
    public void recordProductNotFound(UUID productId) {
        meterRegistry.counter("product.service.call", "status", "not_found", "product_id", productId.toString())
                .increment();
        log.debug("Recorded product not found: {}", productId);
    }

}
