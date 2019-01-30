package io.kermoss.saga.pizzashop.api;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.kermoss.saga.pizzashop.domain.Cart;
import io.kermoss.saga.pizzashop.domain.Pizza;
import io.kermoss.saga.pizzashop.service.BakingService;
import io.kermoss.saga.pizzashop.service.PizzaService;

@RestController
@RequestMapping("/pizza")
public class OrderPizzaController {
    private static final Logger log = LoggerFactory.getLogger(OrderPizzaController.class);
    
	@Autowired
	private BakingService bakingService; 
    @Autowired
	private PizzaService service;

	@GetMapping("/test/order/continue")
	public void continueOn(@RequestParam(name="gtx",required=false) String gtx, @RequestParam(name="cartid",required=false) String cartId) {
		
		log.info("#########Continue#########");
		bakingService.bake(gtx, cartId);
			
	}
	
	@GetMapping("/test/order")
	public void createOrder() {
		
		service.prepareOrder(new Cart("123456", Arrays.asList(new Pizza("Sousou Pizza"))));
	}

}
