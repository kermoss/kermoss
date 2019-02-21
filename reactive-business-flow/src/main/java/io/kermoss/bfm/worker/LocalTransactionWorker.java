package io.kermoss.bfm.worker;

import javax.transaction.Transactional;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Transactional
public abstract class LocalTransactionWorker<F extends BaseTransactionEvent, T extends BaseTransactionEvent, R extends ErrorOccured> {

	protected final WorkerMeta meta;

	public LocalTransactionWorker(final WorkerMeta meta) {
		this.meta = meta;
	}

	@BusinessLocalTransactional
	public abstract  LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent>  onStart(final F onStartEvent);

	@SwitchBusinessLocalTransactional
	public abstract  LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> onNext(final T onNextEvent);

	@RollBackBusinessLocalTransactional
	public abstract LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> onError(final R errorOccured);

	
}
