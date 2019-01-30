package io.kermoss.trx.app.gtx;


import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;


public interface BusinessGlobalTransactionManager {
    // Global Transaction
    void begin(final GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> globalTransactionStepDefinition);
    void commit(final GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> globalTransactionStepDefinition);
}
