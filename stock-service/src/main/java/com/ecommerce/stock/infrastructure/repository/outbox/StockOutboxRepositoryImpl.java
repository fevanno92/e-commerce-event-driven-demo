package com.ecommerce.stock.infrastructure.repository.outbox;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.common.outbox.OutboxStatus;
import com.ecommerce.stock.application.ports.output.StockOutboxRepository;

@Component
public class StockOutboxRepositoryImpl implements StockOutboxRepository {

    private final JpaStockOutboxRepository jpaOutboxRepository;

    @Autowired
    public StockOutboxRepositoryImpl(JpaStockOutboxRepository jpaOutboxRepository) {
        this.jpaOutboxRepository = jpaOutboxRepository;
    }

    @Override
    @Transactional
    public void save(OutboxMessage message) {
        StockOutboxEntity entity = mapToEntity(message);
        jpaOutboxRepository.save(entity);
    }

    @Override
    @Transactional
    public List<OutboxMessage> findAndLockPendingMessages(int batchSize) {
        List<StockOutboxEntity> entities = jpaOutboxRepository.findAndLockPendingMessages(batchSize);
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

    private StockOutboxEntity mapToEntity(OutboxMessage message) {
        return new StockOutboxEntity(
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

    private OutboxMessage mapToMessage(StockOutboxEntity entity) {
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
