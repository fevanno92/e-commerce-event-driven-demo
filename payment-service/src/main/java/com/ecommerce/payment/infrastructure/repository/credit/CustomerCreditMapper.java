package com.ecommerce.payment.infrastructure.repository.credit;

import org.springframework.stereotype.Component;

import com.ecommerce.common.domain.exception.CorruptedDataPersistenceException;
import com.ecommerce.payment.domain.entity.CustomerCredit;
import com.ecommerce.payment.domain.exception.InvalidCreditException;
import com.ecommerce.payment.domain.valueobject.CustomerCreditId;
import com.ecommerce.payment.domain.valueobject.CustomerId;
import com.ecommerce.payment.domain.valueobject.Money;

@Component
public class CustomerCreditMapper {

    public CustomerCreditEntity mapToEntity(CustomerCredit customerCredit) {
        return new CustomerCreditEntity(
                customerCredit.getId().getValue(),
                customerCredit.getCustomerId().getValue(),
                customerCredit.getCreditAmount().getAmount());
    }

    public CustomerCredit mapToDomain(CustomerCreditEntity customerCreditEntity) {
        try {
            return new CustomerCredit(
                    new CustomerCreditId(customerCreditEntity.getId()),
                    new CustomerId(customerCreditEntity.getCustomerId()),
                    new Money(customerCreditEntity.getCreditAmount()));
        } catch (InvalidCreditException e) {
            throw new CorruptedDataPersistenceException(
                    "Customer credit data is database is corrupted for credit id: " + customerCreditEntity.getId(), e);
        }
    }
}
