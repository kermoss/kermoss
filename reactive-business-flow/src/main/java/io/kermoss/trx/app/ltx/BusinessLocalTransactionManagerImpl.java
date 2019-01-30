package io.kermoss.trx.app.ltx;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.app.visitors.localtx.InnerLocalTxStepVisitor;
import io.kermoss.trx.app.visitors.localtx.OuterLocalTxStepVisitor;
import io.kermoss.trx.app.visitors.localtx.StepLocalTxVisitor;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.LocalTransaction;
import io.kermoss.trx.domain.exception.BusinessGlobalTransactionNotFoundException;
import io.kermoss.trx.domain.exception.BusinessLocalTransactionNotFoundException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

@Service
@Transactional
public class BusinessLocalTransactionManagerImpl implements BusinessLocalTransactionManager {

	private final GlobalTransactionRepository globalTransactionRepository;
	private final CommandOrchestrator commandOrchestrator;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final BubbleCache bubbleCache;
	private final TransactionUtilities txUtilities;
	private final Logger logger = LoggerFactory.getLogger(BusinessLocalTransactionManagerImpl.class);


	public BusinessLocalTransactionManagerImpl(GlobalTransactionRepository globalTransactionRepository,
			 CommandOrchestrator commandOrchestrator,
			ApplicationEventPublisher applicationEventPublisher, BubbleCache bubbleCache, TransactionUtilities txUtilities) {
		super();
		this.globalTransactionRepository = globalTransactionRepository;
		this.commandOrchestrator = commandOrchestrator;
		this.applicationEventPublisher = applicationEventPublisher;
		this.bubbleCache = bubbleCache;
		this.txUtilities = txUtilities;
	}

	@Override
	// To refactor, bu searching db for name instead of extracting latest local
	// transaction
	public void begin(final LocalTransactionStepDefinition localTransactionStepDefinition) {
		// First we get our global Transaction
		final LocalTransaction transaction = new LocalTransaction();
		WorkerMeta meta = localTransactionStepDefinition.getMeta();
		transaction.setName(meta.getTransactionName());
		Optional<GlobalTransaction> globalTransaction = this.getGlobalTransaction(localTransactionStepDefinition);
		globalTransaction.ifPresent(
				// Oh there is one
				gtx -> {
					// Verify Idempotent, and execute Supplier only if it succeed!
					// TODO verify idempotence By name, gtx, local variables 
					Optional<LocalTransaction> existedLTX = this.getLocalTransaction(gtx.getId(),transaction.getName());
					if (!existedLTX.isPresent()) {
						transaction.setGlobalTransaction(gtx);
						this.attachToParent(meta,gtx.getId(),transaction);
						this.updateLTX(localTransactionStepDefinition, transaction);
						gtx.addLocalTransaction(transaction);
						globalTransactionRepository.save(gtx);
						localTransactionStepDefinition.accept(buildInnerLocalTxStep(Optional.of(transaction)));
						
					}
				});
		// Global transaction not found! Shame on you.
		globalTransaction.orElseThrow(() -> new BusinessGlobalTransactionNotFoundException(localTransactionStepDefinition));
		localTransactionStepDefinition.accept(buildOuterLocalTxStep(Optional.of(transaction)));

	}

	@Override
	public void commit(final LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition){
		Optional<GlobalTransaction> globalTransaction = this.getGlobalTransaction(localTransactionStepDefinition);
		
		Optional<LocalTransaction> localTransaction = globalTransaction
				.map(gtx -> getLocalTransaction(gtx.getId(),
						localTransactionStepDefinition.getMeta().getTransactionName()))
				.orElseGet(() -> Optional.empty());

		localTransaction.ifPresent(ltx -> {
			if(ltx.getNestedLocalTransactions().stream().map(LocalTransaction::getState).allMatch(s -> s.equals(LocalTransaction.LocalTransactionStatus.COMITTED)))
			{
				if (ltx.getState().equals(LocalTransaction.LocalTransactionStatus.STARTED)) {
					ltx.setState(LocalTransaction.LocalTransactionStatus.COMITTED);
					localTransactionStepDefinition.accept(buildInnerLocalTxStep(localTransaction));
					//TODO make tests without save
					//this.localTransactionRepository.save(ltx);
				}
			localTransactionStepDefinition.accept(buildOuterLocalTxStep(localTransaction));
			}
		});
		localTransaction.orElseThrow(() -> new BusinessLocalTransactionNotFoundException());
	}

	@Override
	public Optional<GlobalTransaction> findGlobalTransaction(final String GTX) {
		return this.globalTransactionRepository.findById(GTX);
	}

	
	StepLocalTxVisitor buildInnerLocalTxStep(Optional<LocalTransaction> localTransaction) {

		VisitorProvision provision = VisitorProvision.builder().localTransaction(localTransaction.get()).build();
		return InnerLocalTxStepVisitor.builder().provision(provision).bubbleCache(bubbleCache).transactionUtilities(txUtilities).
					applicationEventPublisher(applicationEventPublisher).commandOrchestrator(commandOrchestrator).build();
	}
	
	StepLocalTxVisitor buildOuterLocalTxStep(Optional<LocalTransaction> localTransaction) {
		VisitorProvision provision = VisitorProvision.builder().localTransaction(localTransaction.get()).build();
		return OuterLocalTxStepVisitor.builder().provision(provision).bubbleCache(bubbleCache).transactionUtilities(txUtilities).
		applicationEventPublisher(applicationEventPublisher).commandOrchestrator(commandOrchestrator).build();
	}

	Optional<GlobalTransaction> getGlobalTransaction(LocalTransactionStepDefinition localTransactionStepDefinition) {
		Optional<BubbleMessage>  messageOptional = txUtilities.getBubleMessage(localTransactionStepDefinition);
		if(messageOptional.isPresent()) {
			BubbleMessage message = messageOptional.get();
			String GTX = message.getGLTX();
			String PGTX = message.getPGTX();
			if(GTX == null && PGTX != null){
				return globalTransactionRepository.findByNameAndParent(localTransactionStepDefinition.getMeta().getChildOf(), PGTX);
			}else {
				return this.findGlobalTransaction(message.getGLTX());
			}
		}

		throw new BusinessGlobalTransactionNotFoundException(localTransactionStepDefinition);

	}

	Optional<LocalTransaction> getLocalTransaction(String gtx , String name) {

		Optional<LocalTransaction> localTransaction=Optional.empty();
		Optional<GlobalTransaction> globalTransaction = this.globalTransactionRepository.findById(gtx);

		if(globalTransaction.isPresent()){
			GlobalTransaction gt = globalTransaction.get();
			localTransaction=gt.getLocalTransactions().stream().filter(ltx->name.equals(ltx.getName())).findFirst();
		}
		return localTransaction;
	}

	void updateLTX(LocalTransactionStepDefinition pipeline, LocalTransaction ltx){
		Optional<BubbleMessage> bubbleMessage = txUtilities.getBubleMessage(pipeline);
		bubbleMessage.ifPresent( msg -> ltx.setFLTX(msg.getFLTX()));
	}


	void attachToParent(WorkerMeta meta,String gtx ,LocalTransaction nestTransaction){
		
		this.getLocalTransaction(gtx,meta.getChildOf())
		.ifPresent(
				ltx -> {
					ltx.addNestedLocalTransaction(nestTransaction);
				}
		);
	}

}
