package io.kermoss.trx.app.visitors.localtx;

import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.StepVisitor;
import io.kermoss.trx.app.visitors.VisitorProvision;


public abstract class StepLocalTxVisitor extends StepVisitor {

	public StepLocalTxVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher, VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
		super(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
	}

    public StepLocalTxVisitor() {
    }

    public abstract void visit(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline);
}