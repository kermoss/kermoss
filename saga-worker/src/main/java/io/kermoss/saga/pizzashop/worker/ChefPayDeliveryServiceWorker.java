package io.kermoss.saga.pizzashop.worker;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.pipeline.Propagation;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.common.contract.Bill;
import io.kermoss.saga.pizzashop.event.DeliveryBillArrivedEvent;
import io.kermoss.saga.pizzashop.event.DeliveryBillPayed;
import io.kermoss.saga.pizzashop.event.PizzaRejectedEvent;
import io.kermoss.saga.pizzashop.exception.DelayException;
import io.kermoss.saga.pizzashop.exception.ExpensiveException;
import io.kermoss.saga.pizzashop.service.PizzaService;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Component
public class ChefPayDeliveryServiceWorker extends LocalTransactionWorker<DeliveryBillArrivedEvent, DeliveryBillPayed,PizzaRejectedEvent> {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChefPayDeliveryServiceWorker.class);
	@Autowired
	PizzaService pizzaService;

	public ChefPayDeliveryServiceWorker() {
		super(new WorkerMeta("PayDeliveryService", "OrderPizzaService"));
	}

	@Override
	@BusinessLocalTransactional
	public LocalTransactionStepDefinition onStart(DeliveryBillArrivedEvent deliveryBillArrivedEvent) {
		return LocalTransactionStepDefinition.builder().in(deliveryBillArrivedEvent)
				.blow(Stream.of(new DeliveryBillPayed())).receive(Bill.class, x -> {
					System.out.println(x.toString());
//					if (x.getPrice() > 2) {
//                     throw new ExpensiveException("pizza is too much expensive");
//					}
				})
//				.compensateWhen(Propagation.LOCAL,Stream.of(new PizzaRejectedEvent()),DelayException.class, ExpensiveException.class)
//			.compensateWhen(DelayException.class, ExpensiveException.class)
				.receive(Bill.class, (x, y) -> log.info("linking object {} with gtx {}", y, x)).meta(this.meta).build();

	}

	@Override
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition onNext(DeliveryBillPayed deliveryBillPayed) {
		return LocalTransactionStepDefinition.builder().in(deliveryBillPayed).meta(this.meta).build();
	}
	

	@Override
	@RollBackBusinessLocalTransactional
	
	public LocalTransactionStepDefinition onError(PizzaRejectedEvent pizzaRejectedEvent) {
		return LocalTransactionStepDefinition.builder().in(pizzaRejectedEvent).meta(this.meta).build();
	}

}
