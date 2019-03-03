package io.kermoss.trx.domain;

import javax.persistence.*;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "KERMOSS_LTX")
public class LocalTransaction extends State {

	private String FLTX;
	
	private Long bKey;

	@Enumerated(EnumType.STRING)
	private LocalTransactionStatus state = LocalTransactionStatus.STARTED;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gtx_id")
	protected GlobalTransaction globalTransaction;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "ltx_id")
	private Set<LocalTransaction> nestedLocalTransactions = new HashSet<>();

	@java.beans.ConstructorProperties({ "FLTX", "bKey", "state", "globalTransaction", "nestedLocalTransactions",
			"previous" })
	public LocalTransaction(String FLTX, Long bKey, LocalTransactionStatus state, GlobalTransaction globalTransaction,
			Set<LocalTransaction> nestedLocalTransactions, LocalTransaction previous) {
		this.FLTX = FLTX;
		this.bKey = bKey;
		this.state = state;
		this.globalTransaction = globalTransaction;
		this.nestedLocalTransactions = nestedLocalTransactions;

	}

	public LocalTransaction() {
	}

	public String getFLTX() {
		return this.FLTX;
	}

	public Long getbKey() {
		return bKey;
	}

	public void setbKey(Long bKey) {
		this.bKey = bKey;
	}
	
	public void addBusinessKey(Optional<List<String>> businessKey) {
			this.setbKey(BusinessKey.getKey(businessKey));
	}
	

	public LocalTransactionStatus getState() {
		return this.state;
	}

	public GlobalTransaction getGlobalTransaction() {
		return this.globalTransaction;
	}

	public Set<LocalTransaction> getNestedLocalTransactions() {
		return this.nestedLocalTransactions;
	}

	public void setFLTX(String FLTX) {
		this.FLTX = FLTX;
	}

	public void setState(LocalTransactionStatus state) {
		this.state = state;
	}

	public void setGlobalTransaction(GlobalTransaction globalTransaction) {
		this.globalTransaction = globalTransaction;
	}

	public void setNestedLocalTransactions(Set<LocalTransaction> nestedLocalTransactions) {
		this.nestedLocalTransactions = nestedLocalTransactions;
	}

	public enum LocalTransactionStatus {
		STARTED, COMITTED, ROLLBACKED
	}

	public void addNestedLocalTransaction(LocalTransaction nestedLTX) {
		nestedLocalTransactions.add(nestedLTX);
	}
}
