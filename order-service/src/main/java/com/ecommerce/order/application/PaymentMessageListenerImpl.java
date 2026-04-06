package com.ecommerce.order.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.order.application.dto.PaymentFailedCommand;
import com.ecommerce.order.application.dto.PaymentSucceededCommand;
import com.ecommerce.order.application.outbox.OutboxOrderEventSerializer;
import com.ecommerce.order.application.ports.input.PaymentMessageListener;
import com.ecommerce.order.application.ports.output.OrderOutboxRepository;
import com.ecommerce.order.application.ports.output.OrderRepository;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.domain.event.OrderPaidEvent;
import com.ecommerce.order.domain.event.OrderPaymentFailedEvent;
import com.ecommerce.order.domain.valueobject.OrderId;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentMessageListenerImpl implements PaymentMessageListener {

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final OutboxOrderEventSerializer outboxOrderEventSerializer;

    public PaymentMessageListenerImpl(OrderRepository orderRepository,
            OrderOutboxRepository orderOutboxRepository,
            OutboxOrderEventSerializer outboxOrderEventSerializer) {
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.outboxOrderEventSerializer = outboxOrderEventSerializer;
    }

    @Override
    @Transactional
    public void paymentSucceeded(PaymentSucceededCommand command) {
        log.info("Processing successful payment for order: {}", command.orderId());

        Order order = orderRepository.findById(new OrderId(UUID.fromString(command.orderId())))
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + command.orderId()));

        // Idempotency check
        if (order.getStatus() != OrderStatus.RESERVED) {
            log.info("Order {} is not in RESERVED status (current: {}), skipping.", order.getId().getValue(), order.getStatus());
            return;
        }

        // Update Domain State
        order.markAsPaid();
        orderRepository.save(order);

        // Create and save Outbox Event
        OrderPaidEvent paidEvent = new OrderPaidEvent(order);
        OutboxMessage outboxMessage = outboxOrderEventSerializer.createOutboxMessage(paidEvent);
        orderOutboxRepository.save(outboxMessage);

        log.info("Order {} marked as PAID and outbox message saved.", order.getId().getValue());
    }

    @Override
    @Transactional
    public void paymentFailed(PaymentFailedCommand command) {
        log.info("Processing failed payment for order: {}, reason: {}", command.orderId(), command.reason());

        Order order = orderRepository.findById(new OrderId(UUID.fromString(command.orderId())))
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + command.orderId()));

        // Idempotency check
        if (order.getStatus() != OrderStatus.RESERVED) {
            log.info("Order {} is not in RESERVED status (current: {}), skipping.", order.getId().getValue(), order.getStatus());
            return;
        }

        // Update Domain State
        order.markAsPaymentFailed();
        orderRepository.save(order);

        // Create and save Outbox Event
        OrderPaymentFailedEvent failedEvent = new OrderPaymentFailedEvent(order, command.reason());
        OutboxMessage outboxMessage = outboxOrderEventSerializer.createOutboxMessage(failedEvent);
        orderOutboxRepository.save(outboxMessage);

        log.info("Order {} marked as PAYMENT_FAILED and outbox message saved.", order.getId().getValue());
    }
}
