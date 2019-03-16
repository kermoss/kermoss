package io.kermoss.bfm.validator.dummie.ltx;

import java.util.Optional;

import org.slf4j.Logger;

import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

public class ChefCookingServiceWorkerWNMetaDummy
		extends LocalTransactionWorker<OrderPizzaPendingEventDummy, OrderPizzaPendingEventDummy, ErrorLocalOccured> {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChefCookingServiceWorkerWNMetaDummy.class);
	public ChefCookingServiceWorkerWNMetaDummy() {
		super(new WorkerMeta("MakingPizza", "OrderPizzaService"));
	}

	@Override
	@BusinessLocalTransactional
	public LocalTransactionStepDefinition onStart(OrderPizzaPendingEventDummy orderPizzaPendingEvent) {
		return LocalTransactionStepDefinition.builder()
				.in(orderPizzaPendingEvent).
				//.meta(this.meta).
				build();
	}

	@Override
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition onNext(OrderPizzaPendingEventDummy orderPizzaCookedEvent) {
		return LocalTransactionStepDefinition.builder().in(orderPizzaCookedEvent)
				.meta(this.meta).build();
	}

	


	@Override
	@RollBackBusinessLocalTransactional
	public LocalTransactionStepDefinition onError(ErrorLocalOccured errorLocalOccured) {
		return LocalTransactionStepDefinition.builder().in(errorLocalOccured).meta(this.meta).build();
	}
}
