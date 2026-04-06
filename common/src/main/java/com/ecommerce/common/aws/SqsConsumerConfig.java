package com.ecommerce.common.aws;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.common.domain.exception.CorruptedDataPersistenceException;
import com.ecommerce.common.outbox.OutboxException;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

@Configuration
public class SqsConsumerConfig {

    public static final Set<Class<? extends Throwable>> NON_RETRYABLE_EXCEPTIONS = Set.of(
            DatabindException.class,
            JacksonException.class,
            IllegalArgumentException.class,
            CorruptedDataPersistenceException.class,
            OutboxException.class
    );

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(
            SqsAsyncClient sqsAsyncClient, SqsTemplate sqsTemplate) {
        
        SqsMessageListenerContainerFactory<Object> factory = SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .acknowledgementMode(AcknowledgementMode.ON_SUCCESS)
                )
                .build();
        
        factory.setErrorHandler(new SqsErrorHandler<>(sqsTemplate, NON_RETRYABLE_EXCEPTIONS));
        
        return factory;
    }
}
