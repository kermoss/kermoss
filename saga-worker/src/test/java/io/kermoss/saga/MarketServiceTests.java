package io.kermoss.saga;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.saga.pizzashop.service.PizzaService;
import io.kermoss.saga.pizzashop.worker.BakerPizzaServiceWorker;
import io.kermoss.saga.pizzashop.worker.ChefCookingServiceWorker;
import io.kermoss.saga.pizzashop.worker.ChefPayDeliveryServiceWorker;
import io.kermoss.saga.pizzashop.worker.OrderPizzaStateWorker;
import io.kermoss.saga.pizzasship.worker.DeliverBoxWorker;
import io.kermoss.saga.pizzasship.worker.DeliveryWorker;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;


@Transactional
public class MarketServiceTests extends KermossIT {
    @Autowired
    OrderPizzaStateWorker orderPizzaStateWorker;
    @Autowired
    GlobalTransactionRepository globalTransactionRepository;
    @Autowired
    ApplicationEventPublisher publisher;
    @Autowired
    PizzaService pizzaService;
    @Autowired
    ChefCookingServiceWorker chefCookingServiceWorker;
    @Autowired
    DeliverBoxWorker deliverBoxWorker;
    @Autowired
    DeliveryWorker deliveryWorker;
    @Autowired
    BakerPizzaServiceWorker bakerPizzaServiceWorker;
    @Autowired
    ChefPayDeliveryServiceWorker chefPayDeliveryServiceWorker;
    GlobalTransaction sellerService = null ;

    @Before
    public void setUp() {
        GlobalTransaction orderPizzaService = globalTransactionRepository
                .findByNameAndParent("OrderPizzaService" , null).get();

        sellerService = globalTransactionRepository.findByNameAndParent("SellerService" , orderPizzaService.getId()).get();


    }

    @Test
    public void verifySellerServiceStatus(){
        assertEquals(sellerService.getStatus().name() , "STARTED");
    }



}
