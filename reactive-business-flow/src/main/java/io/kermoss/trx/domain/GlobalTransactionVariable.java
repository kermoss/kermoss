package io.kermoss.trx.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "KERMOSS_GTX_VAR")
public class GlobalTransactionVariable implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gtx_id")
    private GlobalTransaction globalTransaction;
    
    @Column(name = "k")
    private String key;
    @Column(name = "v")
    private String value;

    @java.beans.ConstructorProperties({"id", "globalTransaction", "key", "value"})
    public GlobalTransactionVariable(String id, GlobalTransaction globalTransaction, String key, String value) {
        this.id = id;
        this.globalTransaction = globalTransaction;
        this.key = key;
        this.value = value;
    }

    public GlobalTransactionVariable() {
    }

    public static GlobalTransactionVariableBuilder builder() {
        return new GlobalTransactionVariableBuilder();
    }

    public String getId() {
        return this.id;
    }

    public GlobalTransaction getGlobalTransaction() {
        return this.globalTransaction;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGlobalTransaction(GlobalTransaction globalTransaction) {
        this.globalTransaction = globalTransaction;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class GlobalTransactionVariableBuilder {
        private String id;
        private GlobalTransaction globalTransaction;
        private String key;
        private String value;

        GlobalTransactionVariableBuilder() {
        }

        public GlobalTransactionVariable.GlobalTransactionVariableBuilder id(String id) {
            this.id = id;
            return this;
        }

        public GlobalTransactionVariable.GlobalTransactionVariableBuilder globalTransaction(GlobalTransaction globalTransaction) {
            this.globalTransaction = globalTransaction;
            return this;
        }

        public GlobalTransactionVariable.GlobalTransactionVariableBuilder key(String key) {
            this.key = key;
            return this;
        }

        public GlobalTransactionVariable.GlobalTransactionVariableBuilder value(String value) {
            this.value = value;
            return this;
        }

        public GlobalTransactionVariable build() {
            return new GlobalTransactionVariable(id, globalTransaction, key, value);
        }

        public String toString() {
            return "GlobalTransactionVariable.GlobalTransactionVariableBuilder(id=" + this.id + ", globalTransaction=" + this.globalTransaction + ", key=" + this.key + ", value=" + this.value + ")";
        }
    }
}
