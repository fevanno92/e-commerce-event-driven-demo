package com.ecommerce.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ecommerce.payment.infrastructure.repository.credit.CustomerCreditEntity;
import com.ecommerce.payment.infrastructure.repository.credit.JpaCustomerCreditRepository;

@Component
public class DefaultCustomerCredit implements CommandLineRunner {

    private final JpaCustomerCreditRepository customerCreditRepository;

    @Autowired
    public DefaultCustomerCredit(JpaCustomerCreditRepository customerCreditRepository) {
        this.customerCreditRepository = customerCreditRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (customerCreditRepository.count() == 0) {
            customerCreditRepository.save(new CustomerCreditEntity(UUID.randomUUID(), UUID.fromString("b3c525be-a7d3-4aeb-a411-785d603b02e4"), new BigDecimal("15000")));
            customerCreditRepository.save(new CustomerCreditEntity(UUID.randomUUID(), UUID.fromString("d7e2b910-f2f4-48de-9fdf-db3ae27efb90"), new BigDecimal("2000")));
            customerCreditRepository.save(new CustomerCreditEntity(UUID.randomUUID(), UUID.fromString("a64b7c22-7dbf-4e0c-800f-8a48d93f17c5"), new BigDecimal("10")));
        }
    }

}
