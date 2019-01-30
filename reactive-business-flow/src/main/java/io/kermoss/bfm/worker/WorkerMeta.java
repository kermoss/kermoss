package io.kermoss.bfm.worker;


import javax.validation.constraints.NotNull;

public class WorkerMeta {
    @NotNull
    private final String transactionName;
    private String childOf;

    @java.beans.ConstructorProperties({"transactionName"})
    public WorkerMeta(String transactionName) {
        this.transactionName = transactionName;
    }

    @java.beans.ConstructorProperties({"transactionName", "childOf"})
    public WorkerMeta(String transactionName, String childOf) {
        this.transactionName = transactionName;
        this.childOf = childOf;
    }

    @NotNull
    public String getTransactionName() {
        return this.transactionName;
    }

    public String getChildOf() {
        return this.childOf;
    }

    public void setChildOf(String childOf) {
        this.childOf = childOf;
    }
}
