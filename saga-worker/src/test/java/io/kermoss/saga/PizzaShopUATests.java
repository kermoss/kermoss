package io.kermoss.saga;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.cmd.domain.OutboundCommand;
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
public class PizzaShopUATests extends KermossIT {
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

     GlobalTransaction orderpizzaGTX = null;

    @Before
    public void setUp(){
        this.orderpizzaGTX = globalTransactionRepository.findByNameAndParent("OrderPizzaService", null ).get();

    }

    @Test
    public void verifyOrderServicePizzaReceived(){

        assertTrue(globalTransactionRepository.exists(orderpizzaGTX.getId()));

    }
    @Test
    public void verifyMakingPizzaReceived(){

        List<LocalTransaction> ltxList = orderpizzaGTX.getLocalTransactions();
        assertTrue(ltxList.stream().anyMatch(l -> l.getName().equals("MakingPizza")));
    }
    @Test
    public void verfiyBakingPizzaServiceExists(){

        LocalTransaction makingPizza = orderpizzaGTX.getLocalTransactions()
                .stream().filter(l-> l.getName().equals("MakingPizza")).findFirst().get();

       assertTrue(makingPizza.getNestedLocalTransactions().stream().anyMatch( l -> l.getName().equals("BakingPizza")));

    }
    @Test
    public void verifyCmdSentByMakingPizzaToShipDelivered(){

        LocalTransaction makingPizza = orderpizzaGTX.getLocalTransactions()
                .stream().filter(l-> l.getName().equals("MakingPizza")).findFirst().get();

        OutboundCommand cmdMaking = commandRepository.findByLTX(makingPizza.getId() , "shippement-service");

        assertTrue(cmdMaking.getStatus().name().contains("DELIVERED"));

    }
    @Test
    public void makingPizzaAndBakingShouldBeCommited(){
        LocalTransaction makingPizza = orderpizzaGTX.getLocalTransactions()
                .stream().filter(l-> l.getName().equals("MakingPizza")).findFirst().get();
        LocalTransaction bakingPizza = makingPizza.getNestedLocalTransactions().stream().filter(l -> l.getName().equals("BakingPizza")).findFirst().get();

        assertEquals(makingPizza.getState().name() , "COMITTED");
        assertEquals(bakingPizza.getState().name() , "COMITTED");

    }
   @Test
    public void workflowShouldChangeAllGTXState(){


       List<GlobalTransaction> gtxList = globalTransactionRepository.findAll();

        GlobalTransaction firstGTX =  gtxList.get(1);

        GlobalTransaction secondGTX = gtxList.get(2);

      assertEquals(secondGTX.getParent() , firstGTX.getId());

       assertEquals(globalTransactionRepository.count() , 3);

       assertEquals(firstGTX.getStatus().name() , "COMITTED");

       assertEquals(globalTransactionRepository
               .findByNameAndParent("DeliverBoxService" , firstGTX.getId())
               .get().getStatus().name() , "COMITTED");

       assertEquals(globalTransactionRepository
               .findByNameAndParent("SellerService" , firstGTX.getId())
               .get().getStatus().name() , "STARTED");



    }
    @Test
    public void workFlowShouldHandleRelationBtwLTXandGTX(){

        List<LocalTransaction> ltxList = globalTransactionRepository.findAll().stream().flatMap(
                l -> l.getLocalTransactions().stream()).collect(Collectors.toList());


        LocalTransaction firstLTX = ltxList.get(0);


        assertEquals(firstLTX.getName() , ltxList.stream()
                .filter( l -> l.getName().equals("MakingPizza")).findFirst().get().getName());


        assertEquals(ltxList.stream()
                        .filter( l -> l.getName().equals("BoxDelivery")).findFirst().get().getFLTX()
                , ltxList.stream().filter( l -> l.getName().equals("MakingPizza")).findFirst().get().getId());

        assertEquals(ltxList.stream().filter( l -> l.getName().equals("PayDeliveryService")).findFirst().get().getFLTX()
                , ltxList.stream().filter( l -> l.getName().equals("BoxDelivery")).findFirst().get().getId());





    }
    @Test
    public void allLTXShouldHaveMakingPizzaAsGTXexeptBoxDelivery(){
        List<LocalTransaction> ltxList = globalTransactionRepository.findAll().stream().flatMap(
                l -> l.getLocalTransactions().stream()).collect(Collectors.toList());
        List<GlobalTransaction> gtxList = globalTransactionRepository.findAll();
        GlobalTransaction firstGTX = gtxList.get(1);
        assertTrue( ltxList.stream()
                .filter( l -> !l.getName().equals("BoxDelivery"))
                .allMatch( ll -> ll.getGlobalTransaction().getId().equals(firstGTX.getId())) );

    }

    @Test
    public void allLTXshouldBeCommited(){

        List<LocalTransaction> ltxList = globalTransactionRepository.findAll().stream().flatMap(
                l -> l.getLocalTransactions().stream()).collect(Collectors.toList());

        assertTrue( ltxList.stream()
                .allMatch(l -> l.getState().name().equals("COMITTED")));

    }



}