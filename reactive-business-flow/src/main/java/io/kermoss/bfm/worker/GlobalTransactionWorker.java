package io.kermoss.bfm.worker;

import javax.transaction.Transactional;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

@Transactional
public abstract class GlobalTransactionWorker<F extends BaseGlobalTransactionEvent, T extends  BaseGlobalTransactionEvent> {
    protected final WorkerMeta meta;
    public GlobalTransactionWorker(
            final WorkerMeta meta
    ) {
        this.meta = meta;
    }
    @BusinessGlobalTransactional
    abstract public GlobalTransactionStepDefinition onStart(final F onStartEvent);
    @CommitBusinessGlobalTransactional
    abstract public GlobalTransactionStepDefinition onComplete(final T onCompleteEvent);
}
