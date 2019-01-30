package io.kermoss.saga.pizzashop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.kermoss.bfm.app.BusinessFlow;
import io.kermoss.saga.pizzashop.event.BakingPizzaReadyEvent;

@Service
public class BakingService {

    @Autowired
    BusinessFlow businessFlow;
	@Autowired
	Tracer tracer;
	
    private static final Logger log = LoggerFactory.getLogger(BakingService.class);
    @Transactional
    public void bake(String gtx, String cartid){
        System.out.println("Starting baking pizza");
        System.out.println("Pizza of GTX "+ gtx);
        System.out.println("Pizza ready");
        businessFlow.access(gtx, new BakingPizzaReadyEvent(cartid));
        	
    }
}
