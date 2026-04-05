package com.ecommerce.payment.infrastructure.repository.credit;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ecommerce.payment.application.ports.output.CustomerCreditRepository;
import com.ecommerce.payment.domain.entity.CustomerCredit;
import com.ecommerce.payment.domain.valueobject.CustomerId;

@Repository
public class CustomerCreditRepositoryImpl implements CustomerCreditRepository {

    private final JpaCustomerCreditRepository jpaCustomerCreditRepository;
    private final CustomerCreditMapper customerCreditMapper;

    public CustomerCreditRepositoryImpl(JpaCustomerCreditRepository jpaCustomerCreditRepository,
                                        CustomerCreditMapper customerCreditMapper) {
        this.jpaCustomerCreditRepository = jpaCustomerCreditRepository;
        this.customerCreditMapper = customerCreditMapper;
    }

    @Override
    public Optional<CustomerCredit> findByCustomerId(CustomerId customerId) {
        return jpaCustomerCreditRepository.findByCustomerId(customerId.getValue())
                .map(customerCreditMapper::mapToDomain);
    }

    @Override
    public CustomerCredit save(CustomerCredit customerCredit) {
        CustomerCreditEntity customerCreditEntity = customerCreditMapper.mapToEntity(customerCredit);
        CustomerCreditEntity savedEntity = jpaCustomerCreditRepository.save(customerCreditEntity);
        return customerCreditMapper.mapToDomain(savedEntity);
    }
}
