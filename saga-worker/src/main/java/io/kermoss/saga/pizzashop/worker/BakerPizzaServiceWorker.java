package io.kermoss.saga.pizzashop.worker;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.pizzashop.domain.Pizza;
import io.kermoss.saga.pizzashop.event.BakingPizzaPendingEvent;
import io.kermoss.saga.pizzashop.event.BakingPizzaReadyEvent;
import io.kermoss.saga.pizzashop.event.OrderPizzaCookedEvent;
import io.kermoss.saga.pizzashop.service.BakingService;
import io.kermoss.saga.pizzashop.service.PizzaService;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Component
@Profile({"single","shop","test"})
public class BakerPizzaServiceWorker extends LocalTransactionWorker<BakingPizzaPendingEvent, BakingPizzaReadyEvent,ErrorLocalOccured> {

	@Autowired
	PizzaService pizzaService;
	@Autowired
	BakingService bakingService;

	@Autowired
	RestTemplate restTemplate;

	Logger log = LoggerFactory.getLogger(BakerPizzaServiceWorker.class);

	public BakerPizzaServiceWorker() {
		super(new WorkerMeta("BakingPizza", "MakingPizza"));
	}

	@Override
	@BusinessLocalTransactional
	public LocalTransactionStepDefinition onStart(BakingPizzaPendingEvent onStartEvent) {
		return LocalTransactionStepDefinition.builder().in(onStartEvent).process(bakingPizza(onStartEvent.getCartId()))
				// .attach(gtx -> bakingService.bake(gtx, onStartEvent.getCartId()))
				.attach(gtx -> {
					CompletableFuture.supplyAsync(() -> {
						log.info("the baker sleep now zzz! ");
						try {
							Thread.currentThread().sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/pizza/test/order/continue")
								.queryParam("gtx", gtx)
								.queryParam("cartid", onStartEvent.getCartId());
						restTemplate.getForEntity(builder.toUriString(),
								String.class);
						log.info("Ah the baker continue working now ! ");

						return true;
					});

				}).meta(this.meta).build();
	}

	@Override
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition onNext(BakingPizzaReadyEvent onNextEvent) {
		return LocalTransactionStepDefinition.builder().in(onNextEvent).process(bakingPizzaReady(onNextEvent.getCartId()))
				.blow(Stream.of(new OrderPizzaCookedEvent(onNextEvent.getCartId()))).meta(this.meta).build();
	}

	private Optional<Supplier> bakingPizza(String id) {

		log.info("Hey, I'll start baking your pizza {} right now! ", id);

		Supplier s = () -> {
			pizzaService.getOrders().get(id).getPizzas().stream().forEach(p -> {
				p.setPizzaState(Pizza.PizzaState.T7AMRAT);
				log.info("You pizza status is {}", p.getPizzaState());

			});
			return null;
		};
		return Optional.of(s);
	}

	private Optional<Supplier> bakingPizzaReady(String id) {

		Supplier s = () -> {
			pizzaService.getOrders().get(id).getPizzas().stream().forEach(p -> {
				p.setPizzaState(Pizza.PizzaState.TABET);
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
