package io.kermoss.bfm.validator.dummie.gtx;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

public class OrderPizzaStateWorkerWNCDummy extends GlobalTransactionWorker<OrderPizzaReceivedDummy, OrderPizzaReceivedDummy> {
    

    public OrderPizzaStateWorkerWNCDummy() {
        super(new WorkerMeta("OrderPizzaServiceDummy"));
    }

    @Override
    @BusinessGlobalTransactional
    public GlobalTransactionStepDefinition onStart(OrderPizzaReceivedDummy orderPizzaReceivedEvent) {
       
    	return GlobalTransactionStepDefinition.builder()
                .in(orderPizzaReceivedEvent)
                .meta(this.meta)
                .build();
    }

    @Override
    //@CommitBusinessGlobalTransactional
    public GlobalTransactionStepDefinition onComplete(OrderPizzaReceivedDummy orderPizzaReadyEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(orderPizzaReadyEvent)
                .meta(this.meta)
                .build();
    }
}
