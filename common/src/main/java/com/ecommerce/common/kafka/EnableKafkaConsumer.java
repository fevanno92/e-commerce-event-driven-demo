package com.ecommerce.common.kafka;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({KafkaConfigData.class, KafkaConsumerConfigData.class, KafkaConsumerConfig.class})
public @interface EnableKafkaConsumer {
}
