package io.kermoss.trx.app.visitors.localtx;

import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;


public class OuterLocalTxStepVisitor extends StepLocalTxVisitor {
	
	public OuterLocalTxStepVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher,
                                   VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
		super(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
	}

    public static OuterLocalTxStepVisitorBuilder builder() {
        return new OuterLocalTxStepVisitorBuilder();
    }

    @Override
	public void visit(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> localTransactionStepDefinition) {
		// For each Events we save it ID and we create a bubble that contains Transaction information (GTX, LTX and FLTX)
		// and we publish the event for listeners!
		localTransactionStepDefinition.getBlow().forEach(e -> {
			BubbleMessage bubbleMessage = new BubbleMessage(getProvision().getLocalTransaction());
			getBubbleCache().addBubble(e.getId(), bubbleMessage);
			getApplicationEventPublisher().publishEvent(e);
		});

	}

    public static class OuterLocalTxStepVisitorBuilder {
        private BubbleCache bubbleCache;
        private ApplicationEventPublisher applicationEventPublisher;
        private VisitorProvision provision;
        private CommandOrchestrator commandOrchestrator;
        private TransactionUtilities utilities;

        OuterLocalTxStepVisitorBuilder() {
        }

        public OuterLocalTxStepVisitorBuilder bubbleCache(BubbleCache bubbleCache) {
            this.bubbleCache = bubbleCache;
            return this;
        }

        public OuterLocalTxStepVisitorBuilder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public OuterLocalTxStepVisitorBuilder provision(VisitorProvision provision) {
            this.provision = provision;
            return this;
        }

        public OuterLocalTxStepVisitorBuilder commandOrchestrator(CommandOrchestrator commandOrchestrator) {
            this.commandOrchestrator = commandOrchestrator;
            return this;
        }
        public OuterLocalTxStepVisitorBuilder transactionUtilities(TransactionUtilities utilities) {
            this.utilities = utilities;
            return this;
        }

        public OuterLocalTxStepVisitor build() {
            return new OuterLocalTxStepVisitor(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
        }

        public String toString() {
            return "OuterLocalTxStepVisitor.OuterLocalTxStepVisitorBuilder(bubbleCache=" + this.bubbleCache + ", applicationEventPublisher=" + this.applicationEventPublisher + ", provision=" + this.provision + ", commandOrchestrator=" + this.commandOrchestrator + ")";
        }
    }
}
