package io.kermoss.bfm.event;

public class GlobalRollbackOccured extends BaseLocalTransactionEvent{
    private String trxChildOf;
    
	public GlobalRollbackOccured(String trxChildOf) {
		super();
		this.trxChildOf = trxChildOf;
	}

	public GlobalRollbackOccured() {
		super();
	}

	public String getTrxChildOf() {
		return trxChildOf;
	}
	

	

	

}
