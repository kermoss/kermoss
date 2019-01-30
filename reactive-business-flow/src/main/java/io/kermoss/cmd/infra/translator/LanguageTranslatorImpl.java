package io.kermoss.cmd.infra.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.event.InboundCommandPrepared;
import io.kermoss.cmd.domain.event.InboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandNotFoundException;
import io.kermoss.cmd.exception.DecoderNotFoundException;
import io.kermoss.domain.DecoderRegistry;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.infra.KermossTxLogger;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LanguageTranslatorImpl implements LanguageTranslator {
	private static final Logger log = LoggerFactory.getLogger(LanguageTranslatorImpl.class);

	private final BubbleCache bubbleCache;
	private final CommandRepository commandRepository;
	private final ApplicationEventPublisher publisher;
	private final KermossTxLogger txLogger;
	private DecoderRegistry decoders;

	@Autowired
	public LanguageTranslatorImpl(CommandRepository commandRepository, ApplicationEventPublisher publisher,
			BubbleCache bubbleCache, DecoderRegistry decoderRegistry, KermossTxLogger txLogger) {
		this.commandRepository = commandRepository;
		this.publisher = publisher;
		this.bubbleCache = bubbleCache;
		this.decoders = decoderRegistry;
		this.txLogger = txLogger;
	}

	public void registerDecoder(String subject, BaseDecoder baseDecoder) {
		if (!decoders.containsKey(subject)) {
			decoders.put(subject, baseDecoder);
		}

	}
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void onCommandStarted(final InboundCommandStarted commandStarted) {
		final CommandMeta meta = commandStarted.getMeta();
		final String subject = meta.getSubject();
		if (decoders.containsKey(subject)) {
			final BaseDecoder decoder = decoders.get(subject);
			final BaseTransactionEvent event = decoder.decode(meta);
			addBubbleMessage(meta,Stream.of(event));
			this.publisher.publishEvent(event);
			Optional<InboundCommand> inboundCommandOptional = commandRepository.findInboundommandOpt(meta.getCommandId());

			inboundCommandOptional.ifPresent(cmd -> {
			    cmd.changeStatusToCompleted();
				txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for InboundCommand: {}", txstatus, txLogger.printJsonObject(cmd)));
			});
			inboundCommandOptional.orElseThrow(CommandNotFoundException::new);
		}else {
			throw new DecoderNotFoundException(subject);
		}
	}

	@Override
	@EventListener
	public void onCommandPrepared(InboundCommandPrepared commandPrepared) {
		final CommandMeta meta = commandPrepared.getMeta();
		final String subject = meta.getSubject();
		if (decoders.containsKey(subject)) {
			final BaseDecoder decoder = decoders.get(subject);
			final BaseTransactionEvent event = decoder.decode(meta);
			addBubbleMessage(meta,Stream.of(event));
			this.publisher.publishEvent(event);
		}else {
			throw new DecoderNotFoundException(subject);
		}
	}

	public void addBubbleMessage(CommandMeta meta, Stream<BaseTransactionEvent> events) {
		BubbleMessage bubbleMessage = BubbleMessage.builder()
				.LTX(meta.getLTX())
				.GLTX(meta.getGTX())
				.FLTX(meta.getFLTX())
				.PGTX(meta.getPGTX())
				.trace(meta.getTraceId())
				.commande(meta.getCommandId())
				.build();
		events.forEach(e->{
			getBubbleCache().addBubble(e.getId(), bubbleMessage);
		});
		
	}

    public BubbleCache getBubbleCache() {
        return this.bubbleCache;
    }

    public CommandRepository getCommandRepository() {
        return this.commandRepository;
    }

    public ApplicationEventPublisher getPublisher() {
        return this.publisher;
    }

    public Map<String, BaseDecoder> getDecoders() {
        return this.decoders;
    }

	public void setDecoders(DecoderRegistry decoders) {
		this.decoders = decoders;
	}
}
