package io.kermoss.bfm.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;


@Service
@Transactional(propagation = Propagation.MANDATORY)
public class BusinessFlow {

    ApplicationEventPublisher publisher;
    GlobalTransactionRepository globalTransactionRepository;
    BubbleCache bubbleCache;

    @Autowired
    public BusinessFlow(ApplicationEventPublisher publisher, GlobalTransactionRepository globalTransactionRepository, BubbleCache bubbleCache) {
        this.publisher = publisher;
        this.globalTransactionRepository = globalTransactionRepository;
        this.bubbleCache = bubbleCache;
    }

    public  <T extends BaseTransactionEvent> void access(String gtx, T event){
        globalTransactionRepository.findById(gtx).ifPresent( globalTransaction ->
        {
            bubbleCache.addBubble(event.getId(), new BubbleMessage(gtx, null));
            publisher.publishEvent(event);
        });
    }
}