package io.kermoss.bfm.worker;

import javax.transaction.Transactional;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;

@Transactional
public abstract class LocalTransactionWorker<F extends BaseTransactionEvent, T extends BaseTransactionEvent, R extends ErrorOccured> {

	protected final WorkerMeta meta;

	public LocalTransactionWorker(final WorkerMeta meta) {
		this.meta = meta;
	}

	public abstract  LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent>  onStart(final F onStartEvent);

	public abstract  LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> onNext(final T onNextEvent);

	public abstract LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> onError(final R errorOccured);

	public WorkerMeta getMeta() {
		return meta;
	}
	
	

	
}
