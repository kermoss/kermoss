package io.kermoss.trx.app.visitors.localtx;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.event.GlobalRollbackOccured;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition.CompensateWhen;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.pipeline.Propagation;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.domain.Message;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;

public class InnerLocalTxStepVisitor extends StepLocalTxVisitor {
	@Autowired
	CommandRepository commandRepository;
	private final Logger LOG = LoggerFactory.getLogger(InnerLocalTxStepVisitor.class);

	public InnerLocalTxStepVisitor(BubbleCache bubbleCache, ApplicationEventPublisher applicationEventPublisher,
			VisitorProvision provision, CommandOrchestrator commandOrchestrator, TransactionUtilities utilities) {
		super(bubbleCache, applicationEventPublisher, provision, commandOrchestrator, utilities);
	}

	public static InnerLocalTxStepVisitorBuilder builder() {
		return new InnerLocalTxStepVisitorBuilder();
	}

	@Override
	public void visit(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline) {

		try {
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
		} catch (Exception exception) {
			compensatingLocalTransaction(transactionPipeline, exception);
		}

	}

	void compensatingLocalTransaction(
			LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline,
			Exception exception) {
		
		LOG.error("Kermoss Compensate Transaction On ", exception);
		CompensateWhen<Class<Exception>> compensateWhen = transactionPipeline.getCompensateWhen();

		if (compensateWhen != null) {
			Stream<Class<Exception>> exs = Stream.of(compensateWhen.getExceptions());
			if (exs.anyMatch(ex -> ex.isAssignableFrom(exception.getClass()))) {
				Stream<BaseTransactionEvent> blowStream = compensateWhen.getBlow();
				if (blowStream != null) {
					blowStream.forEach(event -> blowEvent(event));
				} else {
					if (!Propagation.GLOBAL.equals(compensateWhen.getPropagation())) {
						LOG.info("Local Kermoss Compensating Transaction");
						Message errorOccured = new ErrorLocalOccured();
						blowEvent(errorOccured);
					}
				}

			}
		}
		if (compensateWhen!=null && Propagation.GLOBAL.equals(compensateWhen.getPropagation())) {
			LOG.info("Kermoss Compensating Transaction: Global Rollback Occured ");
			Message globalRollbackOccured = new GlobalRollbackOccured(transactionPipeline.getMeta().getChildOf());
			blowEvent(globalRollbackOccured);
		}
	}

	void blowEvent(Message message) {
		BubbleMessage bubbleMessage = new BubbleMessage(getProvision().getLocalTransaction());
		getBubbleCache().addBubble(message.getId(), bubbleMessage);
		getApplicationEventPublisher().publishEvent(message);
	}

	void receivePayLoad(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline) {

		if (transactionPipeline.getReceivedCommand() != null) {
			Optional<Object> payload = getCommandOrchestrator().retreive(transactionPipeline.getIn().getId(),
					transactionPipeline.getReceivedCommand().getTarget());

			payload.ifPresent(p -> transactionPipeline.getReceivedCommand().getConsumer().accept(p));
		}

	}

	void receivePayLoadGTX(LocalTransactionStepDefinition<? extends BaseLocalTransactionEvent> transactionPipeline,
			String gtx) {

		final AbstractTransactionStepDefinition.ReceivedCommandGTX receivedCommandGTX = transactionPipeline
				.getReceivedCommandGTX();
		if (receivedCommandGTX != null) {
			Optional<Object> payload = getCommandOrchestrator().retreive(transactionPipeline.getIn().getId(),
					receivedCommandGTX.getTarget());
			payload.ifPresent(p -> transactionPipeline.getReceivedCommandGTX().getConsumer().accept(gtx, p));
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

		public InnerLocalTxStepVisitorBuilder applicationEventPublisher(
				ApplicationEventPublisher applicationEventPublisher) {
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
			return new InnerLocalTxStepVisitor(bubbleCache, applicationEventPublisher, provision, commandOrchestrator,
					utilities);
		}

		public String toString() {
			return "InnerLocalTxStepVisitor.InnerLocalTxStepVisitorBuilder(bubbleCache=" + this.bubbleCache
					+ ", applicationEventPublisher=" + this.applicationEventPublisher + ", provision=" + this.provision
					+ ", commandOrchestrator=" + this.commandOrchestrator + ")";
		}
	}
}