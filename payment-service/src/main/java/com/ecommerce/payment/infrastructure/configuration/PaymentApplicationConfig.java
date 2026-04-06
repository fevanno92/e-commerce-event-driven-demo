package com.ecommerce.payment.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.payment.domain.PaymentDomainService;
import com.ecommerce.payment.domain.PaymentDomainServiceImpl;

@Configuration
public class PaymentApplicationConfig {
    
    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }

}
