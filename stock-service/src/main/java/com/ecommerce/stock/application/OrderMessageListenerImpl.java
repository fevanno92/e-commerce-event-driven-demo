package com.ecommerce.stock.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ecommerce.stock.application.outbox.OutboxStockEventSerializer;
import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.stock.application.dto.OrderItemDTO;
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
}
