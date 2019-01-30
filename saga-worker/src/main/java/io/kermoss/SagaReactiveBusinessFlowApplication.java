package io.kermoss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import io.kermoss.saga.pizzashop.domain.Cart;
import io.kermoss.saga.pizzashop.domain.Pizza;
import io.kermoss.saga.pizzashop.service.PizzaService;

import java.util.Arrays;

@SpringBootApplication
public class SagaReactiveBusinessFlowApplication {

	@Autowired
	PizzaService service;

	public static void main(String[] args) {
		SpringApplication.run(SagaReactiveBusinessFlowApplication.class, args);
	}

//	@Bean
//	CommandLineRunner runner(final Environment environment) {
//		Cart c = new Cart("123456", Arrays.asList(new Pizza("Sousou Pizza")));
//			return args -> {
//			service.prepareOrder(c);
//
//		};
//	}
}
