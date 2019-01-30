package io.kermoss.infra;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Span.SpanBuilder;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.kermoss.trx.domain.GlobalTransaction;

@Component
public class KermossTracer {
	@Autowired

	private Tracer tracer;

	private final Logger log = LoggerFactory.getLogger(KermossTracer.class);
	@Autowired
	private ApplicationEventPublisher publisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void closeSpan(GtxSpanStarted gtxSpanStarted) {
		Span currentSpan = currentSpan();
		log.info("Close current Span" + currentSpan);
		this.closeSpan(currentSpan);

	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void closeSpanE(GtxSpanStarted gtxSpanStarted) {
		closeSpan(currentSpan());
	}

	public Span startGtxSpan(String name, String gtx, String traceId, Span span) {

		if (span == null || !hasGtx(span)) {
			Span newSpan = spanForId(traceId, span);
			Span newGlobalSpan = tracer.createSpan(name, newSpan);
			newGlobalSpan.tag("kermoss.gtx", gtx);
			if (span != null) {
				//span.logEvent("started_ON->"+tracer.getCurrentSpan().traceIdString());
				span.tags().forEach((k, v) -> {
					newGlobalSpan.tag(k, v);
				});
			}
			// newGlobalSpan.logEvent("StartingGtx...");
			publisher.publishEvent(new GtxSpanStarted());

			log.info("Open new Span" + newGlobalSpan);

			return newGlobalSpan;
		}
		return span;

	}

	public Span startGtxSpan(GlobalTransaction gt, Span span) {
		String name = gt.getParent() == null ? "In the parent globalTransaction" : "In the child globalTransaction";
		return startGtxSpan(name, gt.getId(), gt.getTraceId(), span);
	}

	public Span startNestedSpan(String name, String event, Span span) {
		Span nestedSpan = null;
		if (span != null) {
			Span tnestedSpan = tracer.createSpan(name, span);
			tnestedSpan.logEvent(event);
			span.tags().forEach((k, v) -> {
				tnestedSpan.tag(k, v);
			});
			nestedSpan = tnestedSpan;
		}
		return nestedSpan;
	}

	private Span spanForId(final String traceId, final Span parent) {
		SpanBuilder builder = Span.builder();
		return builder.traceId(Span.hexToId(traceId)).spanId((new Random()).nextLong()).exportable(true).build();
	}

	public void closeSpan(Span span) {
		tracer.close(span);
	};

	public Span currentSpan() {
		return tracer.getCurrentSpan();
	};

	private boolean hasGtx(Span span) {
		if (span.tags().containsKey("kermoss.gtx")) {
			return true;
		}
		return false;
	}
}
