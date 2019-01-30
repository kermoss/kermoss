package io.kermoss.saga.pizzashop.event;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;

public class OrderPizzaReceivedEvent extends BaseGlobalTransactionEvent {

    private String cartId;

    @java.beans.ConstructorProperties({"cartId"})
    public OrderPizzaReceivedEvent(String cartId) {
        this.cartId = cartId;
    }

    public OrderPizzaReceivedEvent() {
    }

    public String getCartId() {
        return this.cartId;
    }
}
