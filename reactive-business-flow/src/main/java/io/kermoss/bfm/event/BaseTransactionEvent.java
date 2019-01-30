package io.kermoss.bfm.event;

import io.kermoss.domain.Message;


public abstract class BaseTransactionEvent extends Message {
    public BaseTransactionEvent() {
    }

    public String toString() {
        return "BaseTransactionEvent()";
    }
}
