package io.kermoss.trx.app.gtx;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.cmd.domain.InboundCommand;

public class RequestGlobalTransaction {

	private BaseGlobalTransactionEvent eventRequestor;
	private InboundCommand commandRequestor;
	private String GTX;
	private String parent;
	private String name;
	private String traceId;

	private RequestGlobalTransaction() {

	}

	public String getGTX() {
		return GTX;
	}

	public String getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public String getTraceId() {
		return traceId;
	}

	public BaseGlobalTransactionEvent getEventRequestor() {
		return eventRequestor;
	}

	public InboundCommand getCommandRequestor() {
		return commandRequestor;
	}

	public static class RequestGlobalTransactionBuilder {

		private BaseGlobalTransactionEvent eventRequestor;
		private InboundCommand commandRequestor;
		private String GTX;
		private String parent;
		private String name;
		private String traceId;

		RequestGlobalTransactionBuilder eventRequestor(BaseGlobalTransactionEvent eventRequestor) {
			this.eventRequestor = eventRequestor;
			return this;
		}

		RequestGlobalTransactionBuilder commandRequestor(InboundCommand commandRequestor) {
			this.commandRequestor = commandRequestor;
			return this;
		}

		RequestGlobalTransactionBuilder gtx(String GTX) {
			this.GTX = GTX;
			return this;
		}

		RequestGlobalTransactionBuilder parent(String parent) {
			this.parent = parent;
			return this;
		}

		RequestGlobalTransactionBuilder name(String name) {
			this.name = name;
			return this;
		}

		RequestGlobalTransactionBuilder traceId(String traceId) {
			this.traceId = traceId;
			return this;
		}

		public RequestGlobalTransaction build() {
			RequestGlobalTransaction requestGlobalTransaction = new RequestGlobalTransaction();
			
			requestGlobalTransaction.commandRequestor=commandRequestor;
			requestGlobalTransaction.eventRequestor=eventRequestor;
			requestGlobalTransaction.GTX=GTX;
			requestGlobalTransaction.parent=parent;
			requestGlobalTransaction.name=name;
			requestGlobalTransaction.traceId=traceId;
			
			return requestGlobalTransaction;
		}

	}
}
