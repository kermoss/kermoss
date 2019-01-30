package io.kermoss.saga.pizzashop.event;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;

public class OrderPizzaPendingEvent extends BaseLocalTransactionEvent {
    private String cartID;

    @java.beans.ConstructorProperties({"cartID"})
    public OrderPizzaPendingEvent(String cartID) {
        this.cartID = cartID;
    }

    public OrderPizzaPendingEvent() {
    }

    public String getCartID() {
        return this.cartID;
    }
}
