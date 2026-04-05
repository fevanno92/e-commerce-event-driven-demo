package com.ecommerce.stock.infrastructure.repository.outbox;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.common.outbox.OutboxStatus;

public interface JpaStockOutboxRepository extends JpaRepository<StockOutboxEntity, UUID> {

    /**
     * Retrieve and lock messages pending publication.
     * Uses FOR UPDATE SKIP LOCKED to allow multiple instances to process different messages concurrently.
     * Messages locked by other instances are skipped.
     * 
     * Note: This is a native query because JPQL doesn't support SKIP LOCKED.
     * Compatible with PostgreSQL and MySQL 8+.
     */
    @Query(value = """
            SELECT * FROM stock_outbox 
            WHERE status = 'STARTED' 
            ORDER BY created_at ASC 
            LIMIT :batchSize 
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<StockOutboxEntity> findAndLockPendingMessages(@Param("batchSize") int batchSize);

    /**
     * Update the status of an outbox message.
     */
    @Modifying
    @Query("UPDATE StockOutboxEntity o SET o.status = :status, o.processedAt = :processedAt WHERE o.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") OutboxStatus status,
            @Param("processedAt") Instant processedAt);

    /**
     * Increment the retry count without changing the status.
     */
    @Modifying
    @Query("UPDATE StockOutboxEntity o SET o.retryCount = o.retryCount + 1 WHERE o.id = :id")
    void incrementRetryCount(@Param("id") UUID id);

    /**
     * Delete messages processed before a certain date.
     */
    @Modifying
    @Query("DELETE FROM StockOutboxEntity o WHERE o.status = 'PROCESSED' AND o.processedAt < :cutoffDate")
    void deleteProcessedBefore(@Param("cutoffDate") Instant cutoffDate);
}
