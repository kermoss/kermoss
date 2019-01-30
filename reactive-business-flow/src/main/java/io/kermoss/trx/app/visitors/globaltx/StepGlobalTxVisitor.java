package io.kermoss.trx.app.visitors.globaltx;

import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.StepVisitor;
import io.kermoss.trx.app.visitors.VisitorProvision;

public abstract class StepGlobalTxVisitor extends StepVisitor {

	public StepGlobalTxVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher, VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
		super(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
	}

    public StepGlobalTxVisitor() {
    }

    public abstract void visit(GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> transactionPipeline);
}