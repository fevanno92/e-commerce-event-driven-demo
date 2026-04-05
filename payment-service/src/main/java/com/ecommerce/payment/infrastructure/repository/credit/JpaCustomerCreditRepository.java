package com.ecommerce.payment.infrastructure.repository.credit;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCustomerCreditRepository extends JpaRepository<CustomerCreditEntity, UUID> {
    Optional<CustomerCreditEntity> findByCustomerId(UUID customerId);
}
