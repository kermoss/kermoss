package io.kermoss.infra;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.kermoss.bfm.event.BaseTransactionEvent;

import java.util.Optional;

@Configuration
@Aspect
public class KermossLoggerConfig {
    private static final Logger log = LoggerFactory.getLogger(KermossLoggerConfig.class);
    @Autowired
    BubbleCache bubbleCache;

    @Before("execution(* io.kermoss.bfm.worker.GlobalTransactionWorker.onStart())")
    public void beforeGlobalTransactionWorkeronStart(final JoinPoint joinPoint) {
        this.extractEvent(joinPoint.getArgs()).ifPresent(event -> {
            log.info("GlobalTransactionWorker.onStart Started with event {}", event.getClass().getSimpleName());
        });
    }

    @After("execution(* io.kermoss.bfm.worker.GlobalTransactionWorker.onComplete())")
    public void afterGlobalTransactionWorkerOnComplete(final JoinPoint joinPoint) {
        this.extractEvent(joinPoint.getArgs()).ifPresent(event -> {
            log.info("GlobalTransactionWorker.onComplete Finished with event {}", event.getClass().getSimpleName());
        });
    }


    /**
     *
     * Local Transaction
     */
    @Before("execution(* io.kermoss.bfm.worker.LocalTransactionWorker.onStart(*))")
    public void beforeLocalTransactionWorkerOnStart(final JoinPoint joinPoint) {
        this.extractEvent(joinPoint.getArgs()).ifPresent(event -> {
            log.info("LocalTransactionWorker.onStart Started for GTX {} with event ({})", getGTLX(event.getId()), event.getClass().getSimpleName());
        });
    }

    @After("execution(* io.kermoss.bfm.worker.LocalTransactionWorker.onNext(*))")
    public void afterLocalTransactionWorkerOnNext(final JoinPoint joinPoint) {
        this.extractEvent(joinPoint.getArgs()).ifPresent(event -> {
            log.info("LocalTransactionWorker.onNext Finished for GTX {} with event ({})", getGTLX(event.getId()), event.getClass().getSimpleName());
        });
    }


    private Optional<BaseTransactionEvent> extractEvent(final Object[] args) {
        try {
            return Optional.ofNullable(BaseTransactionEvent.class.cast(args[0]));
        } catch (final Exception e) {
            log.error("Could not extract Event from Pointcut", e);
            return Optional.empty();
        }
    }

    private String getGTLX(String eventId){
        return bubbleCache.getBubble(eventId).isPresent()? bubbleCache.getBubble(eventId).get().getGLTX() : "NO-GTX";
    }
}
