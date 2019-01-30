package io.kermoss.saga.pizzashop.event;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;

public class OrderPizzaCookedEvent extends BaseLocalTransactionEvent {
    String cartId;

    @java.beans.ConstructorProperties({"cartId"})
    public OrderPizzaCookedEvent(String cartId) {
        this.cartId = cartId;
    }

    public OrderPizzaCookedEvent() {
    }

    public String getCartId() {
        return this.cartId;
    }
}
