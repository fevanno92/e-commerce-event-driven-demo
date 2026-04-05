package com.ecommerce.common.outbox.mapper;

import com.ecommerce.common.domain.event.DomainEvent;

/**
 * Interface for mapping a domain event to an outbox payload.
 */
public interface PayloadMapper<T extends DomainEvent> {
    /**
     * @param eventClass The domain event class to check for support.
     * @return true if this mapper can handle the given event type.
     */
    boolean supports(Class<? extends T> eventClass);

    /**
     * Maps the domain event to a payload object (DTO).
     * @param event The domain event instance.
     * @return The payload object to be serialized into the outbox.
     */
    Object mapToPayload(T event);
}
