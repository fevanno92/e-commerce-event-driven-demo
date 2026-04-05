package com.ecommerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ecommerce.common.kafka.EnableKafkaConsumer;
import com.ecommerce.common.kafka.EnableKafkaProducer;

@SpringBootApplication
@EnableKafkaProducer
@EnableKafkaConsumer
@EnableScheduling
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
