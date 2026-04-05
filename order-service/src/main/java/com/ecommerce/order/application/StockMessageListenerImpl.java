package com.ecommerce.order.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.order.application.dto.CancelOrderCommand;
import com.ecommerce.order.application.dto.ValidateOrderCommand;
import com.ecommerce.order.application.outbox.OutboxOrderEventSerializer;
import com.ecommerce.order.application.ports.input.StockMessageListener;
import com.ecommerce.order.application.ports.output.OrderOutboxRepository;
import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.domain.event.OrderCanceledEvent;
import com.ecommerce.order.domain.event.OrderValidatedEvent;
import com.ecommerce.order.domain.valueobject.OrderId;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockMessageListenerImpl implements StockMessageListener {

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final OutboxOrderEventSerializer outboxOrderEventSerializer;

    public StockMessageListenerImpl(OrderRepository orderRepository,
            OrderOutboxRepository orderOutboxRepository,
            OutboxOrderEventSerializer outboxOrderEventSerializer) {
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.outboxOrderEventSerializer = outboxOrderEventSerializer;
    }

    @Override
    @Transactional
    public void validateOrder(ValidateOrderCommand command) {
        log.info("Processing validate order for order: {}", command.orderId());

        Order order = orderRepository.findById(new OrderId(command.orderId()))
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + command.orderId()));

        // Idempotency check, ignore the message if already processed
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info("Order {} is not in PENDING status, skipping.", order.getId().getId());
            return;
        }

        // Update Domain State
        order.validate();
        orderRepository.save(order);

        // Create and save Outbox Event
        OrderValidatedEvent validatedEvent = new OrderValidatedEvent(order);
        OutboxMessage outboxMessage = outboxOrderEventSerializer.createOutboxMessage(validatedEvent);
        orderOutboxRepository.save(outboxMessage);

        log.info("Order {} successfully validated and outbox message saved.", order.getId().getId());
    }

    @Override
    @Transactional
    public void cancelOrder(CancelOrderCommand command) {
        log.info("Processing cancel order for order: {}, reason: {}",
                command.orderId(), command.reason());

        Order order = orderRepository.findById(new OrderId(command.orderId()))
                .orElseThrow(
                        () -> new IllegalArgumentException("Order not found with ID: " + command.orderId()));

        // Idempotency check, ignore the message if already processed
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info("Order {} is not in PENDING status, skipping.", order.getId().getId());
            return;
        }

        // Update Domain State
        order.cancel();
        orderRepository.save(order);

        // Create and save Outbox Event
        OrderCanceledEvent canceledEvent = new OrderCanceledEvent(order, command.reason());
        OutboxMessage outboxMessage = outboxOrderEventSerializer.createOutboxMessage(canceledEvent);
        orderOutboxRepository.save(outboxMessage);

        log.info("Order {} marked as CANCELLED and outbox message saved.", order.getId().getId());
    }
}
