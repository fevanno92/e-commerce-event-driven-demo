package com.ecommerce.stock.application.outbox.mapper;

import com.ecommerce.common.outbox.mapper.PayloadMapper;
import com.ecommerce.stock.domain.event.StockEvent;

/**
 * Interface for mapping a domain event to an outbox payload.
 * Helps in implementing the Open-Closed Principle for the Outbox serializer.
 * Extends the generic PayloadMapper from the common module.
 */
public interface StockEventPayloadMapper extends PayloadMapper<StockEvent> {

    @Override
    boolean supports(Class<? extends StockEvent> eventClass);

    @Override
    Object mapToPayload(StockEvent event);
}
