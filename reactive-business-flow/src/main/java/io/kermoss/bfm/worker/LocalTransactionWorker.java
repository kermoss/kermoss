package io.kermoss.bfm.worker;

import javax.transaction.Transactional;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Transactional
public abstract class LocalTransactionWorker<F extends BaseTransactionEvent, T extends BaseTransactionEvent> {

    protected final WorkerMeta meta;
    public LocalTransactionWorker(
            final WorkerMeta meta
    ) {
        this.meta = meta;
    }

    @BusinessLocalTransactional
    abstract public LocalTransactionStepDefinition onStart(final F onStartEvent);
    @SwitchBusinessLocalTransactional
    abstract public LocalTransactionStepDefinition onNext(final T onNextEvent);
}

