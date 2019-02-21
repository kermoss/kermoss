package io.kermoss.trx.app.ltx;

import java.util.Optional;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.trx.domain.GlobalTransaction;

public interface BusinessLocalTransactionManager {
    //Local Transaction
    void begin(final LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition);
    void commit(final LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition);
    void rollBack(final LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition);
    Optional<GlobalTransaction> findGlobalTransaction(final String GTX);
}
