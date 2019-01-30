package io.kermoss.trx.app.gtx;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.infra.GtxSpanStarted;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.exception.BusinessGlobalTransactionInstableException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

@Service
public class BusinessGlobalTransactionServiceImpl implements BusinessGlobalTransactionService {

	private final GlobalTransactionRepository globalTransactionRepository;

	private final Tracer tracer;
	private ApplicationEventPublisher publisher;

	public BusinessGlobalTransactionServiceImpl() {
		super();
		this.globalTransactionRepository = null;
		this.tracer = null;
	}

	@Autowired
	public BusinessGlobalTransactionServiceImpl(GlobalTransactionRepository globalTransactionRepository,
												Tracer tracer,
												ApplicationEventPublisher publisher) {
		super();
		this.globalTransactionRepository = globalTransactionRepository;
		this.tracer = tracer;
		this.publisher = publisher;
	}

	private boolean isSynchronizedVote(RequestGlobalTransaction rgt) {

		InboundCommand commandRequestor = rgt.getCommandRequestor();
		if (commandRequestor != null) {
			return InboundCommand.Status.PREPARED.equals(commandRequestor.getStatus());
		}

		return false;
	}

	GlobalTransaction startNewGlobalTransaction(RequestGlobalTransaction rgt) {
		
		String traceId = null;
		// stop to continue when starting with un existing GTX
		if (rgt.getGTX() != null) {
			throw new BusinessGlobalTransactionInstableException(rgt);
		}
		// continue with traceId of parent
		traceId = trace(rgt);

		GlobalTransaction globalTransaction = GlobalTransaction.create(rgt.getName(), traceId);
		if (this.isSynchronizedVote(rgt)) {
			globalTransaction.setStatus(GlobalTransaction.GlobalTransactionStatus.PREPARED);
		} else {
			globalTransaction.setStatus(GlobalTransaction.GlobalTransactionStatus.STARTED);
		}
		globalTransaction.setParent(rgt.getParent());
		return globalTransaction;
	}

	String trace(RequestGlobalTransaction rgt) {
		String trace;
		if (rgt.getParent() != null) {
			trace = rgt.getTraceId();
		} else {
			if(tracer.getCurrentSpan()==null){
				trace = tracer.createSpan("inParentGlobalTransaction").traceIdString();
				publisher.publishEvent(new GtxSpanStarted());

			}
			else {
				trace = tracer.getCurrentSpan().traceIdString();
			}
		}
		return trace;
	}


	@Override
	public Optional<GlobalTransaction> findGlobalTransaction(final String id) {
		return Optional.ofNullable(this.globalTransactionRepository.findOne(id));
	}

	
	@Override
	public GlobalTransaction participateToGlobalTransaction(Optional<RequestGlobalTransaction> orgt) {

		GlobalTransaction globalTransaction = null;
		if (orgt.isPresent()) {
			RequestGlobalTransaction rgt = orgt.get();
			Optional<GlobalTransaction> ogtx = globalTransactionRepository.findByNameAndParentAndStatus(rgt.getName(),
					rgt.getParent(), GlobalTransaction.GlobalTransactionStatus.PREPARED);
			if (ogtx.isPresent()) {
				globalTransaction = ogtx.get();
				globalTransaction.setStatus(GlobalTransaction.GlobalTransactionStatus.STARTED);
			} else {
				globalTransaction = this.startNewGlobalTransaction(rgt);
			}

		}

		return globalTransaction;
	}
	
	@Override
	public Optional<GlobalTransaction> retrieveGlobalTransaction(Optional<RequestGlobalTransaction> orgt) {
		Optional<GlobalTransaction> globalTransaction = Optional.empty(); 
		if(orgt.isPresent()){
			RequestGlobalTransaction rgt = orgt.get();
			String id = rgt.getGTX();
			String parentId = rgt.getParent();
			if (id == null && parentId != null) {
				globalTransaction = globalTransactionRepository.findByNameAndParent(rgt.getName(), parentId);
			} else {
				globalTransaction =this.findGlobalTransaction(id);
			}
		}
		
		return globalTransaction;

	}
}
