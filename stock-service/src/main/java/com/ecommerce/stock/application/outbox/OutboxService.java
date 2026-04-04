package com.ecommerce.stock.application.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.stock.application.ports.output.OutboxRepository;
import com.ecommerce.stock.domain.event.StockEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Application service for managing the transactional outbox.
 * This service is responsible for serializing domain events and 
 * saving them to the outbox repository within the current transaction.
 */
@Slf4j
@Service
public class OutboxService {

    private final OutboxEventSerializer outboxEventSerializer;
    private final OutboxRepository outboxRepository;

    public OutboxService(OutboxEventSerializer outboxEventSerializer, 
                         OutboxRepository outboxRepository) {
        this.outboxEventSerializer = outboxEventSerializer;
        this.outboxRepository = outboxRepository;
    }

    /**
     * Save a domain event to the outbox table.
     * 
     * @param event The stock event to persist
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(StockEvent event) {
        log.info("Saving {} for aggregate ID: {} to outbox", 
                event.getEventType(), 
                event.getStockReservation().getOrderId().getId());
        
        OutboxMessage message = outboxEventSerializer.toOutboxMessage(event);
        outboxRepository.save(message);
    }
}
