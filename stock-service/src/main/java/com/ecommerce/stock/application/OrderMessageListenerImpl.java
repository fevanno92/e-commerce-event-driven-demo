package com.ecommerce.stock.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ecommerce.stock.application.outbox.OutboxStockEventSerializer;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.stock.application.dto.ConfirmStockRequest;
import com.ecommerce.stock.application.dto.OrderItemDTO;
import com.ecommerce.stock.application.dto.ReleaseStockRequest;
import com.ecommerce.stock.application.dto.ReserveStockRequest;
import com.ecommerce.stock.application.ports.input.OrderMessageListener;
import com.ecommerce.stock.application.ports.output.StockOutboxRepository;
import com.ecommerce.stock.application.ports.output.StockRepository;
import com.ecommerce.stock.application.ports.output.StockReservationRepository;
import com.ecommerce.stock.domain.StockDomainService;
import com.ecommerce.stock.domain.entity.StockItem;
import com.ecommerce.stock.domain.entity.StockReservation;
import com.ecommerce.stock.domain.entity.StockReservationItem;
import com.ecommerce.stock.domain.entity.StockReservationStatus;
import com.ecommerce.stock.domain.event.StockEvent;
import com.ecommerce.stock.domain.valueobject.OrderId;
import com.ecommerce.stock.domain.valueobject.ProductId;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderMessageListenerImpl implements OrderMessageListener {

    private final StockRepository stockRepository;
    private final StockReservationRepository stockReservationRepository;
    private final StockDomainService stockDomainService;
    private final StockOutboxRepository stockOutboxRepository;
    private final OutboxStockEventSerializer outboxStockEventSerializer;

    public OrderMessageListenerImpl(StockRepository stockRepository,
            StockReservationRepository stockReservationRepository,
            StockDomainService stockDomainService,
            StockOutboxRepository stockOutboxRepository,
            OutboxStockEventSerializer outboxStockEventSerializer) {
        this.stockRepository = stockRepository;
        this.stockReservationRepository = stockReservationRepository;
        this.stockDomainService = stockDomainService;
        this.stockOutboxRepository = stockOutboxRepository;
        this.outboxStockEventSerializer = outboxStockEventSerializer;
    }

    @Override
    @Transactional
    public void reserveStock(ReserveStockRequest request) {
        log.info("Received reserve stock request: {}", request);

        // idempotency check
        if (stockReservationRepository.existsByOrderId(request.orderId())) {
            log.info("Stock reservation already exists for order ID: {}", request.orderId());
            return;
        }

        // sort items by product id to prevent deadlock when multiple orders try to
        // reserve the same stock (pessimistic locking is used)
        List<OrderItemDTO> sortedItems = request.items().stream()
                .sorted(Comparator.comparing(OrderItemDTO::productId))
                .toList();

        // get/create domain entities
        Map<ProductId, StockItem> stockItems = new HashMap<>();
        List<StockReservationItem> stockReservationItems = new ArrayList<>();
        for (OrderItemDTO orderItem : sortedItems) {
            StockItem stockItem = stockRepository.findByProductIdWithLock(orderItem.productId())
                    .orElse(null);
            stockItems.put(new ProductId(orderItem.productId()), stockItem);
            stockReservationItems
                    .add(new StockReservationItem(new ProductId(orderItem.productId()), orderItem.quantity()));
        }
        StockReservation stockReservation = new StockReservation(new OrderId(request.orderId()), stockReservationItems);

        // reserve business logic
        StockEvent event = stockDomainService.validateAndReserveStock(stockReservation, stockItems);

        // save domain entities
        stockReservationRepository.save(stockReservation);
        if (stockReservation.getStatus().equals(StockReservationStatus.CONFIRMED)) {
            stockItems.values().forEach(stockItem -> stockRepository.save(stockItem));
        }

        // use Outbox pattern to ensure reliable event publication
        OutboxMessage outboxMessage = outboxStockEventSerializer.toOutboxMessage(event);
        stockOutboxRepository.save(outboxMessage);
    }

    @Override
    @Transactional
    public void confirmStock(ConfirmStockRequest request) {
        log.info("Finalizing stock for order: {}", request.orderId());

        StockReservation reservation = stockReservationRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Stock reservation not found for order: " + request.orderId()));

        // idempotency check
        if (reservation.getStatus() != StockReservationStatus.CONFIRMED) {
            log.info("Stock reservation for order {} is already finalized or in invalid state: {}", request.orderId(), reservation.getStatus());
            return;
        }

        Map<ProductId, StockItem> stockItems = getStockItemsByProductIdOrdered(reservation);

        StockEvent event = stockDomainService.finalizeStockReservation(reservation, stockItems);
        stockReservationRepository.save(reservation);
        stockItems.values().forEach(stockItem -> stockRepository.save(stockItem));

        OutboxMessage outboxMessage = outboxStockEventSerializer.toOutboxMessage(event);
        stockOutboxRepository.save(outboxMessage);

        log.info("Stock for order {} successfully finalized and outbox message saved.", request.orderId());
    }

    @Override
    @Transactional
    public void releaseStock(ReleaseStockRequest request) {
        log.info("Releasing stock for order: {}", request.orderId());

        StockReservation reservation = stockReservationRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Stock reservation not found for order: " + request.orderId()));

        // idempotency check
        if (reservation.getStatus() != StockReservationStatus.CONFIRMED) {
            log.info("Stock reservation for order {} is already released or in invalid state: {}", request.orderId(), reservation.getStatus());
            return;
        }

        Map<ProductId, StockItem> stockItems = getStockItemsByProductIdOrdered(reservation);

        StockEvent event = stockDomainService.releaseStockReservation(reservation, stockItems);
        stockReservationRepository.save(reservation);
        stockItems.values().forEach(stockItem -> stockRepository.save(stockItem));
        
        OutboxMessage outboxMessage = outboxStockEventSerializer.toOutboxMessage(event);
        stockOutboxRepository.save(outboxMessage);

        log.info("Stock for order {} successfully released and outbox message saved.", request.orderId());
    }

    private Map<ProductId, StockItem> getStockItemsByProductIdOrdered(StockReservation reservation) {
        // sort items by product id to prevent deadlock when multiple orders try to
        // reserve the same stock (pessimistic locking is used)
        List<StockReservationItem> sortedItems = reservation.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProductId().getValue()))
                .toList();

        Map<ProductId, StockItem> stockItems = new HashMap<>();
        for (StockReservationItem stockReservationItem : sortedItems) {
            StockItem stockItem = stockRepository.findByProductIdWithLock(stockReservationItem.getProductId().getValue())
                    .orElse(null);
            stockItems.put(stockReservationItem.getProductId(), stockItem);
        }
        return stockItems;
    }
}
