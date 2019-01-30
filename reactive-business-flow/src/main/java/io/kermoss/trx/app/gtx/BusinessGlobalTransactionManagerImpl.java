package io.kermoss.trx.app.gtx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.app.visitors.globaltx.InnerGlobalTxStepVisitor;
import io.kermoss.trx.app.visitors.globaltx.StepGlobalTxVisitor;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.exception.BusinessGlobalTransactionNotFoundException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class BusinessGlobalTransactionManagerImpl implements BusinessGlobalTransactionManager {

	private final GlobalTransactionRepository globalTransactionRepository;
	private final CommandOrchestrator commandOrchestrator;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final BubbleCache bubbleCache;
	private BusinessGlobalTransactionService businessGlobalTransactionService;
	private GlobalTransactionMapper globalTransactionMapper;
	private TransactionUtilities utilities;

	@Autowired
	public BusinessGlobalTransactionManagerImpl(GlobalTransactionRepository globalTransactionRepository,
			CommandOrchestrator commandOrchestrator, ApplicationEventPublisher applicationEventPublisher,
			BubbleCache bubbleCache , BusinessGlobalTransactionService businessGlobalTransactionService , GlobalTransactionMapper globalTransactionMapper, TransactionUtilities utilities) {
		super();
		this.globalTransactionRepository = globalTransactionRepository;
		this.commandOrchestrator = commandOrchestrator;
		this.applicationEventPublisher = applicationEventPublisher;
		this.bubbleCache = bubbleCache;
		this.globalTransactionMapper = globalTransactionMapper;
		this.businessGlobalTransactionService = businessGlobalTransactionService;
		this.utilities = utilities;
	}

	@Override
	public void begin(GlobalTransactionStepDefinition pipeline) {
		Optional<RequestGlobalTransaction> orgt = globalTransactionMapper.mapTo(pipeline);

		GlobalTransaction globalTransaction = businessGlobalTransactionService.participateToGlobalTransaction(orgt);

		// begin is only responsable to save
		globalTransactionRepository.save(globalTransaction);

		if (globalTransaction.isNew()) {
			pipeline.accept(buildInnerGlobalTxStep(Optional.of(globalTransaction)));
		}

	}

	@Override
	public void commit(GlobalTransactionStepDefinition pipeline) {
		
		Optional<RequestGlobalTransaction> orgt = globalTransactionMapper.mapTo(pipeline);

		Optional<GlobalTransaction> gtx = businessGlobalTransactionService.retrieveGlobalTransaction(orgt);

		gtx.ifPresent(globalTransaction -> {
			if (globalTransaction.getStatus().equals(GlobalTransaction.GlobalTransactionStatus.STARTED)) {
				globalTransaction.setStatus(GlobalTransaction.GlobalTransactionStatus.COMITTED);
				this.globalTransactionRepository.save(globalTransaction);
			}
		});

		gtx.orElseThrow(() -> new BusinessGlobalTransactionNotFoundException(pipeline));

		// TODO commit dont check idempotence // check idempotence by committed
		pipeline.accept(buildInnerGlobalTxStep(gtx));

	}

	private StepGlobalTxVisitor buildInnerGlobalTxStep(Optional<GlobalTransaction> globalTransaction) {
		VisitorProvision provision = VisitorProvision.builder().globalTransaction(globalTransaction.get()).build();
		return InnerGlobalTxStepVisitor.builder().provision(provision).bubbleCache(bubbleCache).transactionUtilities(utilities)
				.applicationEventPublisher(applicationEventPublisher).commandOrchestrator(commandOrchestrator).build();
	}

}
