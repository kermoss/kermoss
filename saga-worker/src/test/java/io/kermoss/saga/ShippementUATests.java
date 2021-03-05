package io.kermoss.saga;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.saga.pizzashop.service.PizzaService;
import io.kermoss.saga.pizzashop.worker.BakerPizzaServiceWorker;
import io.kermoss.saga.pizzashop.worker.ChefCookingServiceWorker;
import io.kermoss.saga.pizzashop.worker.ChefPayDeliveryServiceWorker;
import io.kermoss.saga.pizzashop.worker.OrderPizzaStateWorker;
import io.kermoss.saga.pizzasship.worker.DeliverBoxWorker;
import io.kermoss.saga.pizzasship.worker.DeliveryWorker;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.LocalTransaction;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

@Transactional
public class ShippementUATests extends KermossIT {
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
    @Autowired
    CommandRepository commandRepository;
    GlobalTransaction deliverBoxService = null ;
    LocalTransaction makingPizza = null ;


    @BeforeEach
    public void setUp() {
        GlobalTransaction orderPizzaService = globalTransactionRepository
                .findByNameAndParent("OrderPizzaService" , null).get();
        makingPizza = orderPizzaService.getLocalTransactions().stream().filter(g -> g.getName().equals("MakingPizza")).findFirst().get();
        deliverBoxService = globalTransactionRepository
                .findByNameAndParent("DeliverBoxService" , orderPizzaService.getId() ).get();

    }

    @Test
    public void verifyDeliverServiceReceiveDelivery(){

        InboundCommand inDeliverCmd = commandRepository.findByFLTXAndSubject(makingPizza.getId() , "deliver-pizza");
       assertEquals(inDeliverCmd.getDestination() , "shippement-service");
       assertEquals(inDeliverCmd.getPGTX() , makingPizza.getGlobalTransaction().getId());
    }

    @Test
    public void verifyBoxDeliveryExist(){
        List<LocalTransaction> boxDelivery = deliverBoxService.getLocalTransactions();
        assertTrue(boxDelivery.stream().anyMatch(l -> l.getName().equals("BoxDelivery")));
    }

    @Test
    public void verifyPayDeliveryServiceExist(){
        List<LocalTransaction> ltxList = deliverBoxService.getLocalTransactions();
        LocalTransaction boxDelivery = ltxList.stream()
                .filter(l -> l.getName().equals("BoxDelivery")).findFirst().get();

        assertTrue(boxDelivery.getFLTX().equals(makingPizza.getId()));
    }



}
