package io.kermoss.trx.app.visitors;

import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.LocalTransaction;

public class VisitorProvision {
	
	private LocalTransaction localTransaction;
	private GlobalTransaction globalTransaction;

    @java.beans.ConstructorProperties({"localTransaction", "globalTransaction"})
    VisitorProvision(LocalTransaction localTransaction, GlobalTransaction globalTransaction) {
        this.localTransaction = localTransaction;
        this.globalTransaction = globalTransaction;
    }

    public static VisitorProvisionBuilder builder() {
        return new VisitorProvisionBuilder();
    }

    public LocalTransaction getLocalTransaction() {
        return this.localTransaction;
    }

    public GlobalTransaction getGlobalTransaction() {
        return this.globalTransaction;
    }

    public static class VisitorProvisionBuilder {
        private LocalTransaction localTransaction;
        private GlobalTransaction globalTransaction;

        VisitorProvisionBuilder() {
        }

        public VisitorProvision.VisitorProvisionBuilder localTransaction(LocalTransaction localTransaction) {
            this.localTransaction = localTransaction;
            return this;
        }

        public VisitorProvision.VisitorProvisionBuilder globalTransaction(GlobalTransaction globalTransaction) {
            this.globalTransaction = globalTransaction;
            return this;
        }

        public VisitorProvision build() {
            return new VisitorProvision(localTransaction, globalTransaction);
        }

        public String toString() {
            return "VisitorProvision.VisitorProvisionBuilder(localTransaction=" + this.localTransaction + ", globalTransaction=" + this.globalTransaction + ")";
        }
    }
}
