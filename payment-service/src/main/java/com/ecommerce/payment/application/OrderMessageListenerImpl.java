package com.ecommerce.payment.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ecommerce.common.outbox.OutboxMessage;
import com.ecommerce.payment.application.dto.PaymentRequest;
import com.ecommerce.payment.application.outbox.OutboxPaymentEventSerializer;
import com.ecommerce.payment.application.ports.input.OrderMessageListener;
import com.ecommerce.payment.application.ports.output.PaymentOutboxRepository;
import com.ecommerce.payment.application.ports.output.PaymentRepository;
import com.ecommerce.payment.domain.PaymentDomainService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderMessageListenerImpl implements OrderMessageListener {

    private final PaymentRepository paymentRepository;    
    private final PaymentDomainService paymentDomainService;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final OutboxPaymentEventSerializer outboxPaymentEventSerializer;

    public OrderMessageListenerImpl(PaymentRepository paymentRepository,
            PaymentDomainService paymentDomainService,
            PaymentOutboxRepository paymentOutboxRepository,
            OutboxPaymentEventSerializer outboxPaymentEventSerializer) {
        this.paymentRepository = paymentRepository;
        this.paymentDomainService = paymentDomainService;
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.outboxPaymentEventSerializer = outboxPaymentEventSerializer;
    }

    @Override
    @Transactional
    public void processPayment(PaymentRequest request) {
        
    }
}
