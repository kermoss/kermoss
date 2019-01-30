package io.kermoss.trx.app;

import java.util.Optional;

import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;

@Component
public class TransactionUtilities {
	
	private final BubbleCache bubbleCache;
	
	public TransactionUtilities(BubbleCache bubbleCache) {
		super();
		this.bubbleCache = bubbleCache;
	}

	public Optional<BubbleMessage> getBubleMessage(GlobalTransactionStepDefinition pipeline) {
		String eventId = pipeline.getIn().getId();
		return bubbleCache.getBubble(eventId);
	}

	public Optional<BubbleMessage> getBubleMessage(LocalTransactionStepDefinition pipeline){
		String eventId = pipeline.getIn().getId();
		return bubbleCache.getBubble(eventId);
	}

}