package com.ecommerce.common.kafka;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables Kafka producer configuration for the annotated Spring application.
 * Add this annotation to any @Configuration class or @SpringBootApplication
 * to activate Kafka producer beans from the common module.
 * <p>
 * Required properties in application.yml:
 * <pre>
 * kafka-config:
 *   bootstrap-servers: ...
 *   schema-registry-url-key: schema.registry.url
 *   schema-registry-url: ...
 *
 * kafka-producer-config:
 *   key-serializer-class: ...
 *   value-serializer-class: ...
 *   ...
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({KafkaConfigData.class, KafkaProducerConfigData.class, KafkaProducerConfig.class})
public @interface EnableKafkaProducer {
}
