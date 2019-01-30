package io.kermoss.trx.app.visitors.localtx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;

import java.util.Optional;
import java.util.function.Supplier;

public class InnerLocalTxStepVisitor extends StepLocalTxVisitor  {
    @Autowired
    CommandRepository commandRepository;
	
    public InnerLocalTxStepVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher,
                                   VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
		super(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
	}

    public static InnerLocalTxStepVisitorBuilder builder() {
        return new InnerLocalTxStepVisitorBuilder();
    }


    @Override
	public void visit(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline) {

        String gtx = getProvision().getLocalTransaction().getGlobalTransaction().getId();
        // Handle Process
		transactionPipeline.getProcess().ifPresent(Supplier::get);
		// Send Commands
		transactionPipeline.getSend().forEach(c -> {
			BubbleMessage bubbleMessage = new BubbleMessage(getProvision().getLocalTransaction());
			getBubbleCache().addBubble(c.getId(), bubbleMessage);
			getCommandOrchestrator().receive(c);
		});
        // Handle Attach
        transactionPipeline.getAttach().ifPresent(c -> c.accept(gtx));
        // Handle Payload mapping
        this.receivePayLoad(transactionPipeline);
        // Handle payload with gtx
        this.receivePayLoadGTX(transactionPipeline, gtx);
    }


    void receivePayLoad(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline){

        if(transactionPipeline.getReceivedCommand() != null) {
            Optional<Object> payload = getCommandOrchestrator().retreive(transactionPipeline.getIn().getId(), transactionPipeline.getReceivedCommand().getTarget());

            payload.ifPresent(p -> transactionPipeline.getReceivedCommand().getConsumer().accept(p));
        }

    }

    void receivePayLoadGTX(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline, String gtx){

        final AbstractTransactionStepDefinition.ReceivedCommandGTX receivedCommandGTX = transactionPipeline.getReceivedCommandGTX();
        if(receivedCommandGTX != null) {
            Optional<Object> payload = getCommandOrchestrator().retreive(transactionPipeline.getIn().getId(), receivedCommandGTX.getTarget());
            payload.ifPresent(p -> transactionPipeline.getReceivedCommandGTX().getConsumer().accept(gtx,p));
        }

    }

    public static class InnerLocalTxStepVisitorBuilder {
        private BubbleCache bubbleCache;
        private ApplicationEventPublisher applicationEventPublisher;
        private VisitorProvision provision;
        private CommandOrchestrator commandOrchestrator;
        private TransactionUtilities utilities;

        InnerLocalTxStepVisitorBuilder() {
        }

        public InnerLocalTxStepVisitorBuilder bubbleCache(BubbleCache bubbleCache) {
            this.bubbleCache = bubbleCache;
            return this;
        }

        public InnerLocalTxStepVisitorBuilder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public InnerLocalTxStepVisitorBuilder provision(VisitorProvision provision) {
            this.provision = provision;
            return this;
        }

        public InnerLocalTxStepVisitorBuilder commandOrchestrator(CommandOrchestrator commandOrchestrator) {
            this.commandOrchestrator = commandOrchestrator;
            return this;
        }

        public InnerLocalTxStepVisitorBuilder transactionUtilities(TransactionUtilities utilities) {
            this.utilities = utilities;
            return this;
        }

        public InnerLocalTxStepVisitor build() {
            return new InnerLocalTxStepVisitor(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
        }

        public String toString() {
            return "InnerLocalTxStepVisitor.InnerLocalTxStepVisitorBuilder(bubbleCache=" + this.bubbleCache + ", applicationEventPublisher=" + this.applicationEventPublisher + ", provision=" + this.provision + ", commandOrchestrator=" + this.commandOrchestrator + ")";
        }
    }
}