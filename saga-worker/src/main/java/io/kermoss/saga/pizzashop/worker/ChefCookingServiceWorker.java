package io.kermoss.saga.pizzashop.worker;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.common.contract.ToDilever;
import io.kermoss.saga.pizzashop.cmd.OrderPizzaReadyForShipmentCommand;
import io.kermoss.saga.pizzashop.domain.Pizza;
import io.kermoss.saga.pizzashop.event.BakingPizzaPendingEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaCookedEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaPendingEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaReadyEvent;
import io.kermoss.saga.pizzashop.service.PizzaService;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Component
public class ChefCookingServiceWorker extends LocalTransactionWorker<OrderPizzaPendingEvent, OrderPizzaCookedEvent,ErrorLocalOccured> {

    

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChefCookingServiceWorker.class);
    @Autowired
    PizzaService pizzaService;
    public ChefCookingServiceWorker() {
        super(new WorkerMeta("MakingPizza", "OrderPizzaService"));
    }

    @Override
    @BusinessLocalTransactional
    public LocalTransactionStepDefinition onStart(OrderPizzaPendingEvent orderPizzaPendingEvent) {
        return LocalTransactionStepDefinition.builder()
                .in(orderPizzaPendingEvent)
                .process(makePizzaSupplier(orderPizzaPendingEvent.getCartID()))
                .blow(Stream.of(new BakingPizzaPendingEvent(orderPizzaPendingEvent.getCartID())))
                .meta(this.meta)
                .build();
    }

    @Override
    @SwitchBusinessLocalTransactional
    public LocalTransactionStepDefinition onNext(OrderPizzaCookedEvent orderPizzaCookedEvent) {
        ToDilever b = new ToDilever("Some address", pizzaService.getOrders().get(orderPizzaCookedEvent.getCartId()).getPizzas()
                .stream().map(Pizza::getName)
                .collect(Collectors.toList()));
        OrderPizzaReadyForShipmentCommand cmd = new OrderPizzaReadyForShipmentCommand("deliver-pizza", "",
                b, "shippement-service");
        return LocalTransactionStepDefinition.builder()
                .in(orderPizzaCookedEvent)
                .process(makePizzaReadySupplier(orderPizzaCookedEvent.getCartId()))
                .blow(Stream.of(new OrderPizzaReadyEvent(orderPizzaCookedEvent.getCartId())))
                .send(Stream.of(cmd))
                .meta(this.meta)
                .build();
    }

    private Optional<Supplier> makePizzaSupplier(String id){
        log.info("Hey, I'll start cooking your pizza {} right now! ", id);

        Supplier s = () -> {
            pizzaService.getOrders().get(id).getPizzas().stream().forEach(p -> {
                p.setPizzaState(Pizza.PizzaState.AJINA);
                log.info("You pizza status is {}", p.getPizzaState());
            });
            return null;
        };
        return Optional.of(s);
    }

    private Optional<Supplier> makePizzaReadySupplier(String id){
        log.info("Hey, your pizza {} is ready! ", id);

        Supplier s = () -> {
            pizzaService.getOrders().get(id).getPizzas().stream().forEach(p -> {
                p.setPizzaState(Pizza.PizzaState.WAJDAT);
                log.info("You pizza status is {}", p.getPizzaState());

            });
            return null;
        };
        return Optional.of(s);
    }

	@Override
	@RollBackBusinessLocalTransactional
	public LocalTransactionStepDefinition onError(ErrorLocalOccured errorLocalOccured) {
		return LocalTransactionStepDefinition.builder().in(errorLocalOccured).meta(this.meta).build();
	}
}
