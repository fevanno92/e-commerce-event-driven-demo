package com.ecommerce.order.infrastructure.repository.outbox;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.outbox.OutboxStatus;
import com.ecommerce.order.application.ports.output.OrderOutboxRepository;

@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final JpaOrderOutboxRepository jpaOutboxRepository;

    @Autowired
    public OrderOutboxRepositoryImpl(JpaOrderOutboxRepository jpaOutboxRepository) {
        this.jpaOutboxRepository = jpaOutboxRepository;
    }

    @Override
    @Transactional
    public void save(OutboxMessage message) {
        OrderOutboxEntity entity = mapToEntity(message);
        jpaOutboxRepository.save(entity);
    }

    @Override
    @Transactional
    public List<OutboxMessage> findAndLockPendingMessages(int batchSize) {
        List<OrderOutboxEntity> entities = jpaOutboxRepository.findAndLockPendingMessages(batchSize);
        return entities.stream()
                .map(this::mapToMessage)
                .toList();
    }

    @Override
    @Transactional
    public void updateStatus(UUID id, OutboxStatus status) {
        Instant processedAt = (status == OutboxStatus.PROCESSED) ? Instant.now() : null;
        jpaOutboxRepository.updateStatus(id, status, processedAt);
    }

    @Override
    @Transactional
    public void incrementRetryCount(UUID id) {
        jpaOutboxRepository.incrementRetryCount(id);
    }

    @Override
    @Transactional
    public void deleteProcessedMessages(int retentionDays) {
        Instant cutoffDate = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        jpaOutboxRepository.deleteProcessedBefore(cutoffDate);
    }

    private OrderOutboxEntity mapToEntity(OutboxMessage message) {
        return new OrderOutboxEntity(
                message.getId(),
                message.getAggregateType(),
                message.getAggregateId(),
                message.getEventType(),
                message.getPayload(),
                message.getCreatedAt(),
                message.getTracingContext(),
                message.getStatus(),
                message.getProcessedAt(),
                message.getRetryCount()
        );
    }

    private OutboxMessage mapToMessage(OrderOutboxEntity entity) {
        return new OutboxMessage(
                entity.getId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getPayload(),
                entity.getCreatedAt(),
                entity.getTracingContext(),
                entity.getStatus(),
                entity.getProcessedAt(),
                entity.getRetryCount()
        );
    }
}
