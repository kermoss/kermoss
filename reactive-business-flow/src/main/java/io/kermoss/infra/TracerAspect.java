package io.kermoss.infra;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

@Aspect
@Component
@Order(value=Ordered.LOWEST_PRECEDENCE+1000)
public class TracerAspect {

	@Autowired
	private KermossTracer tracer;
	@Autowired
	private BubbleCache bubbleCache;

	@Autowired
	private GlobalTransactionRepository globalTransactionRepository;

	@Pointcut("execution(* io.kermoss.bfm.app.BusinessFlow.access(..))")
	public void access() {
	}

	@Pointcut("execution(* io.kermoss.trx.app.ltx.BusinessLocalTransactionManagerImpl.begin(..))")
	public void beginLocalTransaction() {
	}

	@Pointcut("execution(* io.kermoss.trx.app.ltx.BusinessLocalTransactionManagerImpl.commit(..))")
	public void commitLocalTransaction() {
	}
	
	@Pointcut("execution(* io.kermoss.trx.app.gtx.BusinessGlobalTransactionManagerImpl.commit(..))")
	public void commitGlobalransaction() {
	}
	


	@Around("access(){}")
	public void arroundAccess(ProceedingJoinPoint pjp) throws Throwable {
		String gtx = (String) pjp.getArgs()[0];
		Span olderSpan = tracer.currentSpan();
		Span nestedGtxSpan = null;
		try {
			Optional<GlobalTransaction> globalTransaction = globalTransactionRepository.findById(gtx);
			if (globalTransaction.isPresent()) {
				GlobalTransaction gt = globalTransaction.get();
				Span gtxSpan = tracer.startGtxSpan(gt, olderSpan);
				nestedGtxSpan = tracer.startNestedSpan(olderSpan.getName(), "Accessing_BFM_From:", gtxSpan);
			}
			pjp.proceed();
		} finally {
			tracer.closeSpan(nestedGtxSpan);
		}
	}

	@Around("beginLocalTransaction(){}")
	public void arroundBeginLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
		LocalTransactionStepDefinition ltp = (LocalTransactionStepDefinition) pjp.getArgs()[0];
		Optional<BubbleMessage> bubbleMessage = bubbleCache.getBubble(ltp.getIn().getId());
		String gltx = bubbleMessage.get().getGLTX();

		Span olderSpan = tracer.currentSpan();
		Span nestedGtxSpan = null;
		try {
			Optional<GlobalTransaction> globalTransaction = globalTransactionRepository.findById(gltx);

			if (globalTransaction.isPresent()) {
				GlobalTransaction gt = globalTransaction.get();
				Span gtxSpan = tracer.startGtxSpan(gt, olderSpan);
				nestedGtxSpan = tracer.startNestedSpan(
						"b-ltx of " + ltp.getMeta().getTransactionName() + " childof: " + ltp.getMeta().getChildOf(),
						ltp.getIn().getClass().getSimpleName(), gtxSpan);
			}
			pjp.proceed();
		} finally {
			tracer.closeSpan(nestedGtxSpan);
		}
	}

	@Around("commitLocalTransaction(){}")
	public void arroundCommitLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
		LocalTransactionStepDefinition ltp = (LocalTransactionStepDefinition) pjp.getArgs()[0];
		Optional<BubbleMessage> bubbleMessage = bubbleCache.getBubble(ltp.getIn().getId());
		String gltx = bubbleMessage.get().getGLTX();

		Span olderSpan = tracer.currentSpan();
		Span nestedGtxSpan = null;
		try {
			Optional<GlobalTransaction> globalTransaction = globalTransactionRepository.findById(gltx);

			if (globalTransaction.isPresent()) {
				GlobalTransaction gt = globalTransaction.get();
				Span gtxSpan = tracer.startGtxSpan(gt, olderSpan);
				nestedGtxSpan = tracer.startNestedSpan(
						"c-ltx of " + ltp.getMeta().getTransactionName() + " childof: " + ltp.getMeta().getChildOf(),
						ltp.getIn().getClass().getSimpleName(), gtxSpan);
			}
			pjp.proceed();
		} finally {
			tracer.closeSpan(nestedGtxSpan);
		}
	}
	
	
	@Around("commitGlobalransaction(){}")
	public void arroundCommitGlobalTransaction(ProceedingJoinPoint pjp) throws Throwable {
		GlobalTransactionStepDefinition gtp = (GlobalTransactionStepDefinition) pjp.getArgs()[0];
		Optional<BubbleMessage> bubbleMessage = bubbleCache.getBubble(gtp.getIn().getId());
		String gltx = bubbleMessage.get().getGLTX();

		Span olderSpan = tracer.currentSpan();
		Span nestedGtxSpan = null;
		try {
			Optional<GlobalTransaction> globalTransaction = globalTransactionRepository.findById(gltx);

			if (globalTransaction.isPresent()) {
				GlobalTransaction gt = globalTransaction.get();
				String name =gt.getParent()==null ? "commit parent gtx of " : "commit child gtx of ";
				Span gtxSpan = tracer.startGtxSpan(gt, olderSpan);
				nestedGtxSpan = tracer.startNestedSpan(
						name + gtp.getMeta().getTransactionName(),
						gtp.getIn().getClass().getSimpleName(), gtxSpan);
			}
			pjp.proceed();
		} finally {
			tracer.closeSpan(nestedGtxSpan);
		}
	}

}
