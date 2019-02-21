package io.kermoss.trx.app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.infra.KermossTracer;
import io.kermoss.trx.app.exception.PoincutDefinitionException;
import io.kermoss.trx.app.ltx.BusinessLocalTransactionManager;

@Aspect
@Component
public class BusinessLocalTransactionAspect {
	
	@Autowired
	KermossTracer tracer;

    final BusinessLocalTransactionManager businessLocalTransactionManager;

    public BusinessLocalTransactionAspect(BusinessLocalTransactionManager businessTransactionManager){
        this.businessLocalTransactionManager = businessTransactionManager;
    }

    @Pointcut("@annotation(io.kermoss.trx.app.annotation.BusinessLocalTransactional)")
    public void localTransactionPointcut(){
        throw new PoincutDefinitionException();
    }

    @Pointcut("@annotation(io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional)")
    public void moveLocalTransactionPointcut(){
        throw new PoincutDefinitionException();
    }
    
    @Pointcut("@annotation(io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional)")
    public void rollBackLocalTransactionPointcut(){
        throw new PoincutDefinitionException();
    }


    @Around("localTransactionPointcut()")
    public void beginLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        LocalTransactionStepDefinition pipeline = (LocalTransactionStepDefinition) pjp.proceed();
        this.businessLocalTransactionManager.begin(pipeline);
       
    }

    @Around("moveLocalTransactionPointcut()")
    public void moveLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        LocalTransactionStepDefinition pipeline = (LocalTransactionStepDefinition) pjp.proceed();
        this.businessLocalTransactionManager.commit(pipeline);
    }
    
    @Around("rollBackLocalTransactionPointcut()")
    public void rollBackTransaction(ProceedingJoinPoint pjp) throws Throwable {
        LocalTransactionStepDefinition pipeline = (LocalTransactionStepDefinition) pjp.proceed();
        this.businessLocalTransactionManager.rollBack(pipeline);
    }
}
