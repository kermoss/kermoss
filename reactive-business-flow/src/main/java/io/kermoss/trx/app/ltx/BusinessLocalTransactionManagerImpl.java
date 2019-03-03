package io.kermoss.trx.app.ltx;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorLocalOccured;
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
import io.kermoss.trx.domain.BusinessKey;
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
			CommandOrchestrator commandOrchestrator, ApplicationEventPublisher applicationEventPublisher,
			BubbleCache bubbleCache, TransactionUtilities txUtilities) {
		super();
		this.globalTransactionRepository = globalTransactionRepository;
		this.commandOrchestrator = commandOrchestrator;
		this.applicationEventPublisher = applicationEventPublisher;
		this.bubbleCache = bubbleCache;
		this.txUtilities = txUtilities;
	}

	@Override
	public void begin(final LocalTransactionStepDefinition localTransactionStepDefinition) {
		final LocalTransaction transaction = new LocalTransaction();
		WorkerMeta meta = localTransactionStepDefinition.getMeta();
		transaction.setName(meta.getTransactionName());
		Optional<GlobalTransaction> globalTransaction = this.getGlobalTransaction(localTransactionStepDefinition);
		globalTransaction.ifPresent(
				gtx -> {
					Optional businessKey = localTransactionStepDefinition.getBusinessKey();
					Optional<LocalTransaction> existedLTX = this.getLocalTransaction(gtx.getId(), transaction.getName(),
							businessKey);
					if (!existedLTX.isPresent()) {
						transaction.setGlobalTransaction(gtx);
						this.attachToParent(meta, gtx.getId(), businessKey,
								transaction);
						this.updateLTX(localTransactionStepDefinition, transaction);
						gtx.addLocalTransaction(transaction);
						globalTransactionRepository.save(gtx);
						localTransactionStepDefinition.accept(buildInnerLocalTxStep(Optional.of(transaction)));

					}
				});
		globalTransaction
				.orElseThrow(() -> new BusinessGlobalTransactionNotFoundException(localTransactionStepDefinition));
		localTransactionStepDefinition.accept(buildOuterLocalTxStep(Optional.of(transaction)));

	}

	@Override
	public void commit(
			final LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition) {
		Optional<GlobalTransaction> globalTransaction = this.getGlobalTransaction(localTransactionStepDefinition);

		Optional<LocalTransaction> localTransaction = globalTransaction.map(
				gtx -> {
					Optional<List<String>> businessKey = localTransactionStepDefinition.getBusinessKey();
					return getLocalTransaction(gtx.getId(), localTransactionStepDefinition.getMeta().getTransactionName(),
							businessKey);
				})
				.orElseGet(() -> Optional.empty());

		localTransaction.ifPresent(ltx -> {
			if (ltx.getNestedLocalTransactions().stream().map(LocalTransaction::getState)
					.allMatch(s -> s.equals(LocalTransaction.LocalTransactionStatus.COMITTED))) {
				if (ltx.getState().equals(LocalTransaction.LocalTransactionStatus.STARTED)) {
					ltx.setState(LocalTransaction.LocalTransactionStatus.COMITTED);
					localTransactionStepDefinition.accept(buildInnerLocalTxStep(localTransaction));
				}
				localTransactionStepDefinition.accept(buildOuterLocalTxStep(localTransaction));
			}
		});
		localTransaction.orElseThrow(() -> new BusinessLocalTransactionNotFoundException());
	}

	@Override
	public void rollBack(
			LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition) {
		Optional<GlobalTransaction> globalTransaction = this.getGlobalTransaction(localTransactionStepDefinition);

		if (localTransactionStepDefinition.getIn().getClass().equals(ErrorLocalOccured.class)) {
			List<LocalTransaction> localTransactions = globalTransaction.map(gtx -> getLocalTransaction(gtx.getId(),
					localTransactionStepDefinition.getMeta().getTransactionName())).get();

			if (localTransactions != null) {
				localTransactions.forEach(ltx -> markAsRollbacked(ltx,localTransactionStepDefinition));
			}
		} else {
			Optional<LocalTransaction> localTransaction = globalTransaction.map(gtx -> getLocalTransaction(gtx.getId(),
					localTransactionStepDefinition.getMeta().getTransactionName(),
					localTransactionStepDefinition.getBusinessKey())).orElseGet(() -> Optional.empty());

			localTransaction.ifPresent(ltx -> markAsRollbacked(ltx,localTransactionStepDefinition));

		}

	}
	
	@Override
	public Optional<GlobalTransaction> findGlobalTransaction(final String GTX) {
		return this.globalTransactionRepository.findById(GTX);
	}
	
	void markAsRollbacked(LocalTransaction ltx,LocalTransactionStepDefinition<? extends BaseTransactionEvent> localTransactionStepDefinition) {
		if (!ltx.getState().equals(LocalTransaction.LocalTransactionStatus.ROLLBACKED)) {
			ltx.setState(LocalTransaction.LocalTransactionStatus.ROLLBACKED);
			localTransactionStepDefinition.accept(buildInnerLocalTxStep(Optional.of(ltx)));
		}
		localTransactionStepDefinition.accept(buildOuterLocalTxStep(Optional.of(ltx)));
	}


	StepLocalTxVisitor buildInnerLocalTxStep(Optional<LocalTransaction> localTransaction) {

		VisitorProvision provision = VisitorProvision.builder().localTransaction(localTransaction.get()).build();
		return InnerLocalTxStepVisitor.builder().provision(provision).bubbleCache(bubbleCache)
				.transactionUtilities(txUtilities).applicationEventPublisher(applicationEventPublisher)
				.commandOrchestrator(commandOrchestrator).build();
	}

	StepLocalTxVisitor buildOuterLocalTxStep(Optional<LocalTransaction> localTransaction) {
		VisitorProvision provision = VisitorProvision.builder().localTransaction(localTransaction.get()).build();
		return OuterLocalTxStepVisitor.builder().provision(provision).bubbleCache(bubbleCache)
				.transactionUtilities(txUtilities).applicationEventPublisher(applicationEventPublisher)
				.commandOrchestrator(commandOrchestrator).build();
	}

	Optional<GlobalTransaction> getGlobalTransaction(LocalTransactionStepDefinition localTransactionStepDefinition) {
		Optional<BubbleMessage> messageOptional = txUtilities.getBubleMessage(localTransactionStepDefinition);
		if (messageOptional.isPresent()) {
			BubbleMessage message = messageOptional.get();
			String GTX = message.getGLTX();
			String PGTX = message.getPGTX();
			if (GTX == null && PGTX != null) {
				return globalTransactionRepository
						.findByNameAndParent(localTransactionStepDefinition.getMeta().getChildOf(), PGTX);
			} else {
				return this.findGlobalTransaction(message.getGLTX());
			}
		}

		throw new BusinessGlobalTransactionNotFoundException(localTransactionStepDefinition);

	}

	Optional<LocalTransaction> getLocalTransaction(String gtx, String name, Optional<List<String>> businessKey) {

		Optional<LocalTransaction> localTransaction = Optional.empty();
		Long key = BusinessKey.getKey(businessKey);
		Optional<GlobalTransaction> globalTransaction = this.globalTransactionRepository.findById(gtx);

		if (globalTransaction.isPresent()) {
			GlobalTransaction gt = globalTransaction.get();
			localTransaction = gt.getLocalTransactions().stream().filter(ltx -> {
				return name.equals(ltx.getName()) && (key != null ? key.equals(ltx.getbKey()) : true);
			}).findFirst();
		}
		return localTransaction;
	}

	List<LocalTransaction> getLocalTransaction(String gtx, String name) {
		List<LocalTransaction> localTransaction = null;
		Optional<GlobalTransaction> globalTransaction = this.globalTransactionRepository.findById(gtx);

		if (globalTransaction.isPresent()) {
			GlobalTransaction gt = globalTransaction.get();
			localTransaction = gt.getLocalTransactions().stream().filter(ltx -> {
				return name.equals(ltx.getName());
			}).collect(Collectors.toList());
		}
		return localTransaction;
	}

	void updateLTX(LocalTransactionStepDefinition pipeline, LocalTransaction ltx) {
		Optional<List<String>> businessKey = pipeline.getBusinessKey();
		ltx.addBusinessKey(businessKey);
		Optional<BubbleMessage> bubbleMessage = txUtilities.getBubleMessage(pipeline);
		bubbleMessage.ifPresent(msg -> ltx.setFLTX(msg.getFLTX()));
	}

	void attachToParent(WorkerMeta meta, String gtx, Optional<List<String>> businessKey,
			LocalTransaction nestTransaction) {

		this.getLocalTransaction(gtx, meta.getChildOf(), businessKey).ifPresent(ltx -> {
			ltx.addNestedLocalTransaction(nestTransaction);
		});
	}

}
