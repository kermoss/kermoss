package io.kermoss.trx.app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.trx.app.exception.PoincutDefinitionException;
import io.kermoss.trx.app.gtx.BusinessGlobalTransactionManager;

@Aspect
@Component
public class BusinessGlobalTransactionAspect {

    final BusinessGlobalTransactionManager businessTransactionManager;
    final ApplicationEventPublisher applicationEventPublisher;

    public BusinessGlobalTransactionAspect(BusinessGlobalTransactionManager businessTransactionManager, ApplicationEventPublisher applicationEventPublisher){
        this.businessTransactionManager = businessTransactionManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Pointcut("@annotation(io.kermoss.trx.app.annotation.BusinessGlobalTransactional)")
    public void globalTransactionPointcut(){
        throw new PoincutDefinitionException();

    }

    @Pointcut("@annotation(io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional)")
    public void commitLocalTransactionPointcut(){
        throw new PoincutDefinitionException();
    }


    @Around("globalTransactionPointcut()")
    public void beginLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        this.businessTransactionManager.begin(pipeline);

    }

    @Around("commitLocalTransactionPointcut()")
    public void moveLocalTransaction(ProceedingJoinPoint pjp) throws Throwable {
        GlobalTransactionStepDefinition pipeline = (GlobalTransactionStepDefinition) pjp.proceed();
        this.businessTransactionManager.commit(pipeline);
    }
}
