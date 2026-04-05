package com.ecommerce.payment.infrastructure.repository.payment;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ecommerce.payment.application.ports.output.PaymentRepository;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.valueobject.OrderId;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentRepositoryImpl(JpaPaymentRepository jpaPaymentRepository, PaymentMapper paymentMapper) {
        this.jpaPaymentRepository = jpaPaymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = paymentMapper.mapToEntity(payment);
        PaymentEntity savedEntity = jpaPaymentRepository.save(paymentEntity);
        return paymentMapper.mapToDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return jpaPaymentRepository.findByOrderId(orderId.getValue())
                .map(paymentMapper::mapToDomain);
    }
}
