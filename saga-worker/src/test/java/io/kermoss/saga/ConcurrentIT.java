package io.kermoss.saga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.kermoss.saga.pizzashop.domain.Cart;
import io.kermoss.saga.pizzashop.domain.Pizza;
import io.kermoss.saga.pizzashop.service.PizzaService;


public class ConcurrentIT extends KermossIT {

	private static final Logger log = LoggerFactory.getLogger(ConcurrentIT.class);
	@Autowired
	private PizzaService service;

	//@Test
	//DeadLock checking ...
	public void testIfAllWorkNicely() throws InterruptedException, ExecutionException {
		List<Cart> c = new ArrayList<>();
		long nowInMillis = System.currentTimeMillis();
		for (int i = 0; i <1; i++) {
			c.add(new Cart(UUID.randomUUID().toString(), Arrays.asList(new Pizza("Pizza num "+i))));
		}
		
		List<CompletableFuture<Boolean>> cFeatures = c.stream().map(cart->{
			CompletableFuture<Boolean> cf = CompletableFuture.supplyAsync(() -> {
				service.prepareOrder(cart);
				return true;
			});
			return cf;
		}).collect(Collectors.toList());
		
//		CompletableFuture<Boolean>[] CfArr = cFeatures.toArray(new CompletableFuture[cFeatures.size()]);
//		CompletableFuture.allOf(CfArr).join();
		
		cFeatures.forEach(cf->{
			cf.join();
			
		});
		
		Thread.currentThread().sleep(12000);
		
		log.info("tested in ==> {} ",(System.currentTimeMillis()-nowInMillis));
		
	}

	@Test
	public void test(){

	}
	
	
}
