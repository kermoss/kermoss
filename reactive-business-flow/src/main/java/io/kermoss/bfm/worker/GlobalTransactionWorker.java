package io.kermoss.bfm.worker;

import javax.transaction.Transactional;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;

@Transactional
public abstract class GlobalTransactionWorker<F extends BaseGlobalTransactionEvent, T extends  BaseGlobalTransactionEvent> {
    protected final WorkerMeta meta;
    public GlobalTransactionWorker(
            final WorkerMeta meta
    ) {
        this.meta = meta;
    }
    
    public abstract  GlobalTransactionStepDefinition onStart(final F onStartEvent);
    public abstract  GlobalTransactionStepDefinition onComplete(final T onCompleteEvent);
    
    public WorkerMeta getMeta() {
		return meta;
	}
}
