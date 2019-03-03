package io.kermoss.trx.domain;

import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "KERMOSS_GTX")
public class GlobalTransaction {
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;
	
	protected Long timestamp = new Date().getTime();

	

	@NotNull
	private String name;

	private String parent;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "globalTransaction")
	private Set<GlobalTransactionVariable> variables = new HashSet<>();

	@Enumerated(EnumType.STRING)
	private GlobalTransactionStatus status = GlobalTransactionStatus.STARTED;

	@OneToMany(mappedBy = "globalTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<LocalTransaction> localTransactions = new ArrayList<>();

	@Transient
	private boolean isNew = false;

	private String traceId;

	@java.beans.ConstructorProperties({ "id", "timestamp", "variables", "status", "localTransactions" })
	public GlobalTransaction(String id,  Long timestamp, Set<GlobalTransactionVariable> variables,
			GlobalTransactionStatus status, List<LocalTransaction> localTransactions) {
		this.id = id;
		this.timestamp = timestamp;
		this.variables = variables;
		this.status = status;
		this.localTransactions = localTransactions;
	}

	GlobalTransaction() {
	}

	public static GlobalTransactionBuilder builder() {
		return new GlobalTransactionBuilder();
	}

	public void addLocalTransaction(final LocalTransaction localTransaction) {
		if (this.localTransactions == null) {
			this.localTransactions = new ArrayList<>();
		}
		this.localTransactions.add(localTransaction);
	}

	public void addVariable(final GlobalTransactionVariable variable) {
		if (this.variables == null) {
			this.variables = new HashSet<>();
		}
		this.variables.add(variable);
	}

	public void addVariable(final String key, final String value) {
		this.addVariable(GlobalTransactionVariable.builder().key(key).value(value).build());
	}


	public Optional<String> getVariableValue(final String key) {
		return this.variables.stream().filter(v -> v.getKey().equals(key)).map(GlobalTransactionVariable::getValue)
				.findAny();
	}

	public String getId() {
		return this.id;
	}
     
	
	
	public Long getTimestamp() {
		return this.timestamp;
	}


	public Set<GlobalTransactionVariable> getVariables() {
		return this.variables;
	}

	public GlobalTransactionStatus getStatus() {
		return this.status;
	}

	public List<LocalTransaction> getLocalTransactions() {
		return this.localTransactions;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}


	public void setVariables(Set<GlobalTransactionVariable> variables) {
		this.variables = variables;
	}

	public void setStatus(GlobalTransactionStatus status) {
		this.status = status;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean aNew) {
		isNew = aNew;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public void setLocalTransactions(List<LocalTransaction> localTransactions) {
		this.localTransactions = localTransactions;
	}

	public static GlobalTransaction create(String name, String traceId) {
		GlobalTransaction gtx = new GlobalTransaction();
		gtx.setNew(true);
		gtx.setName(name);
		gtx.setTraceId(traceId);
		return gtx;
	}

	public enum GlobalTransactionStatus {
		PREPARED, STARTED, COMITTED, ROLLBACKED
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static class GlobalTransactionBuilder {
		private String id;
		private Long bKey;
		private String name;
		private Long timestamp;
		private GlobalTransaction previous;
		private Set<GlobalTransactionVariable> variables;
		private GlobalTransactionStatus status;
		private List<LocalTransaction> localTransactions;

		GlobalTransactionBuilder() {
		}

		public GlobalTransaction.GlobalTransactionBuilder id(String id) {
			this.id = id;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder bKey(Long bKey) {
			this.bKey = bKey;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder timestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder previous(GlobalTransaction previous) {
			this.previous = previous;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder variables(Set<GlobalTransactionVariable> variables) {
			this.variables = variables;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder status(GlobalTransactionStatus status) {
			this.status = status;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder localTransactions(List<LocalTransaction> localTransactions) {
			this.localTransactions = localTransactions;
			return this;
		}

		public GlobalTransaction.GlobalTransactionBuilder name(String name) {
			this.name = name;
			return this;
		}

		public GlobalTransaction build() {
			return new GlobalTransaction(id, timestamp, variables, status, localTransactions);
		}

		public String toString() {
			return "GlobalTransaction.GlobalTransactionBuilder(id=" + this.id + ", timestamp=" + this.timestamp
					+ ", previous=" + this.previous + ", variables=" + this.variables + ", status=" + this.status
					+ ", localTransactions=" + this.localTransactions + ")";
		}
	}
}
