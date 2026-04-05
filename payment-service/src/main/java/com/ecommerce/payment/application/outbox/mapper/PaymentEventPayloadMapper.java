package com.ecommerce.payment.application.outbox.mapper;

import com.ecommerce.common.outbox.mapper.PayloadMapper;
import com.ecommerce.payment.domain.event.PaymentEvent;

/**
 * Interface for mapping a domain event to an outbox payload.
 * Helps in implementing the Open-Closed Principle for the Outbox serializer.
 * Extends the generic PayloadMapper from the common module.
 */
public interface PaymentEventPayloadMapper extends PayloadMapper<PaymentEvent> {

    @Override
    boolean supports(Class<? extends PaymentEvent> eventClass);

    @Override
    Object mapToPayload(PaymentEvent event);
}
