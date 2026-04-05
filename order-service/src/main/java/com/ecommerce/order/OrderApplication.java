package com.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ecommerce.common.kafka.EnableKafkaConsumer;
import com.ecommerce.common.kafka.EnableKafkaProducer;
import com.ecommerce.common.tracing.EnableCommonTracing;

@SpringBootApplication
@EnableKafkaProducer
@EnableKafkaConsumer
@EnableCommonTracing
@EnableScheduling
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

}
