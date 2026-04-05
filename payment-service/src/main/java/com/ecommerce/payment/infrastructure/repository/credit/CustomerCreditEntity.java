package com.ecommerce.payment.infrastructure.repository.credit;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_credits")
public class CustomerCreditEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "credit_amount", nullable = false)
    private BigDecimal creditAmount;

    protected CustomerCreditEntity() {
    }

    public CustomerCreditEntity(UUID id, UUID customerId, BigDecimal creditAmount) {
        this.id = id;
        this.customerId = customerId;
        this.creditAmount = creditAmount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }
}
