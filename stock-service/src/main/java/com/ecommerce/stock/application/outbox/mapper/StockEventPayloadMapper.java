package com.ecommerce.stock.application.outbox.mapper;

import com.ecommerce.stock.domain.event.StockEvent;

/**
 * Interface for mapping a domain event to an outbox payload.
 * Helps in implementing the Open-Closed Principle for the Outbox serializer.
 */
public interface StockEventPayloadMapper {
    /**
     * @param eventClass The domain event class to check for support.
     * @return true if this mapper can handle the given event type.
     */
    boolean supports(Class<? extends StockEvent> eventClass);

    /**
     * Maps the domain event to a payload object (DTO).
     * @param event The domain event instance.
     * @return The payload object to be serialized into the outbox.
     */
    Object mapToPayload(StockEvent event);
}
