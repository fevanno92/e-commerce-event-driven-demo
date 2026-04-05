package com.ecommerce.payment.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.payment.application.dto.PaymentRequest;
import com.ecommerce.payment.application.outbox.OutboxPaymentEventSerializer;
import com.ecommerce.payment.application.ports.input.OrderMessageListener;
import com.ecommerce.payment.application.ports.output.CustomerCreditRepository;
import com.ecommerce.payment.application.ports.output.PaymentOutboxRepository;
import com.ecommerce.payment.application.ports.output.PaymentRepository;
import com.ecommerce.payment.domain.PaymentDomainService;
import com.ecommerce.payment.domain.entity.CustomerCredit;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.entity.PaymentStatus;
import com.ecommerce.payment.domain.event.PaymentEvent;
import com.ecommerce.payment.domain.valueobject.CustomerId;
import com.ecommerce.payment.domain.valueobject.Money;
import com.ecommerce.payment.domain.valueobject.OrderId;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderMessageListenerImpl implements OrderMessageListener {

    private final PaymentRepository paymentRepository;
    private final CustomerCreditRepository customerCreditRepository;
    private final PaymentDomainService paymentDomainService;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final OutboxPaymentEventSerializer outboxPaymentEventSerializer;

    public OrderMessageListenerImpl(PaymentRepository paymentRepository,
            CustomerCreditRepository customerCreditRepository,
            PaymentDomainService paymentDomainService,
            PaymentOutboxRepository paymentOutboxRepository,
            OutboxPaymentEventSerializer outboxPaymentEventSerializer) {
        this.paymentRepository = paymentRepository;
        this.customerCreditRepository = customerCreditRepository;
        this.paymentDomainService = paymentDomainService;
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.outboxPaymentEventSerializer = outboxPaymentEventSerializer;
    }

    @Override
    @Transactional
    public void processPayment(PaymentRequest request) {

        OrderId orderId = new OrderId(request.orderId());
        CustomerId customerId = new CustomerId(request.customerId());

        // idempotency check
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            log.info("Payment already exists for order id: {}", request.orderId());
            return;
        }

        Optional<CustomerCredit> customerCredit = customerCreditRepository.findByCustomerId(customerId);
        Payment payment = new Payment(
                orderId,
                customerId,
                new Money(request.amount()),
                PaymentStatus.PENDING);

        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, customerCredit);

        paymentRepository.save(payment);
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            customerCredit.ifPresent(customerCreditRepository::save);
        }

        OutboxMessage outboxMessage = outboxPaymentEventSerializer.toOutboxMessage(paymentEvent);
        paymentOutboxRepository.save(outboxMessage);

    }
}
