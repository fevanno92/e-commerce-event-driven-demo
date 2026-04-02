package com.ecommerce.order.application.ports.output;

import java.util.UUID;

public interface OrderMetrics {
    /**
     * Record successful order creation
     */
    public void recordOrderCreationSuccess();

    /**
     * Record failed order creation with error reason
     */
    public void recordOrderCreationFailure(String errorReason);

    /**
     * Record product service call timing and status
     */
    public void recordProductServiceCall(UUID productId, long durationMs, boolean success);

    /**
     * Record product not found error
     */
    public void recordProductNotFound(UUID productId);
}
