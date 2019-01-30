package io.kermoss.trx.app.visitors.globaltx;

import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.domain.GlobalTransaction;

import java.util.Optional;
import java.util.function.Supplier;

public class InnerGlobalTxStepVisitor extends StepGlobalTxVisitor  {

    public InnerGlobalTxStepVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher,
                                    VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
		super(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
	}

    public static InnerGlobalTxStepVisitorBuilder builder() {
        return new InnerGlobalTxStepVisitorBuilder();
    }

    @Override
	public void visit(GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> transactionPipeline) {
		transactionPipeline.getProcess().ifPresent(Supplier::get);
		String FLTX = null, PGTX = null;
		Optional<BubbleMessage> msg = getUtilities().getBubleMessage(transactionPipeline);
		if(msg.isPresent()){
           FLTX = msg.get().getFLTX();
           PGTX = msg.get().getPGTX();
        }
		// Send Commands
        sendCommands(transactionPipeline, FLTX, PGTX);
    }
    
    
    //@TODO TO BE REFCATORED
    void sendCommands(GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> transactionPipeline, String FLTX, String PGTX) {
        BubbleMessage bubbleMessage = this.buildBubbleMessgae(getProvision().getGlobalTransaction(), FLTX, PGTX);
        String gtx = getProvision().getGlobalTransaction().getId();

        // Handle payload mapping
        this.receivePayLoad(transactionPipeline);

        //Handle payload with gtx
        this.receivePayLoadGTX(transactionPipeline, gtx);

        // Handle send
        transactionPipeline.getSend().forEach(c -> {
            getBubbleCache().addBubble(c.getId(), bubbleMessage);
            getCommandOrchestrator().receive(c);
        });

        // Handle Prepare
        transactionPipeline.getPrepare().forEach(c -> {
        	getBubbleCache().addBubble(c.getId(), bubbleMessage);
            getCommandOrchestrator().prepare(c);
        });

        // Handle Blow
        transactionPipeline.getBlow().forEach(e -> {
            getBubbleCache().addBubble(e.getId(), bubbleMessage);
            getApplicationEventPublisher().publishEvent(e);
        });
        // Handle attach
        transactionPipeline.getAttach().ifPresent(c -> c.accept(gtx));

    }

    BubbleMessage buildBubbleMessgae(GlobalTransaction globalTransaction, String FLTX, String PGTX){
    	return BubbleMessage.builder()
                .GLTX(globalTransaction.getId())
                .trace(globalTransaction.getTraceId())
                .FLTX(FLTX)
                .PGTX(PGTX)
                .build();
    }

    void receivePayLoad(GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> transactionPipeline){

        final AbstractTransactionStepDefinition.ReceivedCommand<Object> receivedCommand = transactionPipeline.getReceivedCommand();
        if(receivedCommand != null) {
            Optional<Object> payload = getCommandOrchestrator().retreive(transactionPipeline.getIn().getId(), receivedCommand.getTarget());

            payload.ifPresent(p -> receivedCommand.getConsumer().accept(p));
        }

    }

    void receivePayLoadGTX(GlobalTransactionStepDefinition<? extends BaseGlobalTransactionEvent> transactionPipeline, String gtx){

        final AbstractTransactionStepDefinition.ReceivedCommandGTX receivedCommandGTX = transactionPipeline.getReceivedCommandGTX();
        if(receivedCommandGTX != null) {
            Optional<Object> payload = getCommandOrchestrator().retreive(transactionPipeline.getIn().getId(), receivedCommandGTX.getTarget());
            payload.ifPresent(p -> receivedCommandGTX.getConsumer().accept(gtx,p));
        }

    }

    public static class InnerGlobalTxStepVisitorBuilder {
        private BubbleCache bubbleCache;
        private ApplicationEventPublisher applicationEventPublisher;
        private VisitorProvision provision;
        private CommandOrchestrator commandOrchestrator;
        private TransactionUtilities utilities;

        InnerGlobalTxStepVisitorBuilder() {
        }

        public InnerGlobalTxStepVisitorBuilder bubbleCache(BubbleCache bubbleCache) {
            this.bubbleCache = bubbleCache;
            return this;
        }

        public InnerGlobalTxStepVisitorBuilder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public InnerGlobalTxStepVisitorBuilder provision(VisitorProvision provision) {
            this.provision = provision;
            return this;
        }

        public InnerGlobalTxStepVisitorBuilder commandOrchestrator(CommandOrchestrator commandOrchestrator) {
            this.commandOrchestrator = commandOrchestrator;
            return this;
        }

        public InnerGlobalTxStepVisitorBuilder transactionUtilities(TransactionUtilities utilities) {
            this.utilities = utilities;
            return this;
        }

        public InnerGlobalTxStepVisitor build() {
            return new InnerGlobalTxStepVisitor(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
        }

        public String toString() {
            return "InnerGlobalTxStepVisitor.InnerGlobalTxStepVisitorBuilder(bubbleCache=" + this.bubbleCache + ", applicationEventPublisher=" + this.applicationEventPublisher + ", provision=" + this.provision + ", commandOrchestrator=" + this.commandOrchestrator + ")";
        }
    }
}