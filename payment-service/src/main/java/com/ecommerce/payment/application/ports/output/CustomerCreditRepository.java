package com.ecommerce.payment.application.ports.output;

import java.util.Optional;

import com.ecommerce.payment.domain.entity.CustomerCredit;
import com.ecommerce.payment.domain.valueobject.CustomerId;

public interface CustomerCreditRepository {
    CustomerCredit save(CustomerCredit customerCredit);
    Optional<CustomerCredit> findByCustomerId(CustomerId customerId);
}
