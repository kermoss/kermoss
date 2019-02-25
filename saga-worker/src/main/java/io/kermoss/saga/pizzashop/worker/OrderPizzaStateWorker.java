package io.kermoss.saga.pizzashop.worker;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.common.contract.Need;
import io.kermoss.saga.pizzashop.cmd.CallMarketCommand;
import io.kermoss.saga.pizzashop.event.OrderPizzaPendingEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaReadyEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaReadyForShipmentEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaReceivedEvent;
import io.kermoss.saga.pizzashop.service.PizzaService;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

@Component
@Profile({"single","shop"})
public class OrderPizzaStateWorker extends GlobalTransactionWorker<OrderPizzaReceivedEvent, OrderPizzaReadyEvent> {
    @Autowired
    PizzaService pizzaService;

    public OrderPizzaStateWorker() {
        super(new WorkerMeta("OrderPizzaService"));
    }

    @Override
    @BusinessGlobalTransactional
    public GlobalTransactionStepDefinition onStart(OrderPizzaReceivedEvent orderPizzaReceivedEvent) {
        Need need = new Need(Arrays.asList("Tomatos", "mozzarella cheese", "meat"));
        CallMarketCommand cmd = new CallMarketCommand("call-market", "",
                need, "market-service");
       return GlobalTransactionStepDefinition.builder()
                .in(orderPizzaReceivedEvent)
                .meta(this.meta)
                .process(Optional.empty())
                .prepare(Stream.of(cmd))
                .blow(Stream.of(new OrderPizzaPendingEvent(orderPizzaReceivedEvent.getCartId())))
                .attach(gtx -> System.out.println("Attaching external process with gtx : " + gtx + " to OrderPizzaStateWorker"))
                .build();
    }

    @Override
    @CommitBusinessGlobalTransactional
    public GlobalTransactionStepDefinition onComplete(OrderPizzaReadyEvent orderPizzaReadyEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(orderPizzaReadyEvent)
                .meta(this.meta)
                .blow(Stream.of(new OrderPizzaReadyForShipmentEvent()))
                .process(Optional.empty())
                .build();
    }
}
