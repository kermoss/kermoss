package io.kermoss.bfm.event;

public class GlobalTransactionLevelRollbacked extends BaseLocalTransactionEvent {
	private String trxChildOf;
	
	public GlobalTransactionLevelRollbacked(String trxChildOf) {
		super();
		this.trxChildOf = trxChildOf;
	}

	
	public GlobalTransactionLevelRollbacked() {
		super();
	}

	public String getTrxChildOf() {
		return trxChildOf;
	}

}