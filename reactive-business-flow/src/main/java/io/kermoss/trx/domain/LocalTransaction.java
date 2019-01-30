package io.kermoss.trx.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "KERMOSS_LTX")
public class LocalTransaction extends State {
    private String FLTX;
    @Enumerated(EnumType.STRING)
    private LocalTransactionStatus state = LocalTransactionStatus.STARTED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gtx_id")
    protected GlobalTransaction globalTransaction;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ltx_id")
    private Set<LocalTransaction> nestedLocalTransactions = new HashSet<>();
   
//    @OneToOne
//    protected LocalTransaction previous;

    @java.beans.ConstructorProperties({"FLTX", "state", "globalTransaction", "nestedLocalTransactions", "previous"})
    public LocalTransaction(String FLTX, LocalTransactionStatus state, GlobalTransaction globalTransaction, Set<LocalTransaction> nestedLocalTransactions, LocalTransaction previous) {
        this.FLTX = FLTX;
        this.state = state;
        this.globalTransaction = globalTransaction;
        this.nestedLocalTransactions = nestedLocalTransactions;
//        this.previous = previous;
    }

    public LocalTransaction() {
    }

    public String getFLTX() {
        return this.FLTX;
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

//    public LocalTransaction getPrevious() {
//        return this.previous;
//    }

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

//    public void setPrevious(LocalTransaction previous) {
//        this.previous = previous;
//    }

    public enum LocalTransactionStatus {
        STARTED,
        COMITTED,
        ROLLBACKED
    }

    public void addNestedLocalTransaction(LocalTransaction nestedLTX){
        nestedLocalTransactions.add(nestedLTX);
    }
}
