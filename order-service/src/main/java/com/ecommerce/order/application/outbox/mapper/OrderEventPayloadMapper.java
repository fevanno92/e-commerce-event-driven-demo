package com.ecommerce.order.application.outbox.mapper;

import com.ecommerce.common.outbox.mapper.PayloadMapper;
import com.ecommerce.order.domain.event.OrderEvent;

/**
 * Interface for mapping an Order domain event to an outbox payload.
 * Helps in implementing the Open-Closed Principle for the Outbox serializer.
 */
public interface OrderEventPayloadMapper extends PayloadMapper<OrderEvent> {

    @Override
    boolean supports(Class<? extends OrderEvent> eventClass);

    @Override
    Object mapToPayload(OrderEvent event);
}
