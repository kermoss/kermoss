package io.kermoss.saga.pizzashop.event;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;

public class BakingPizzaReadyEvent extends BaseLocalTransactionEvent {

    String cartId;

    public BakingPizzaReadyEvent(String cartId) {
        this.cartId = cartId;
    }

    public BakingPizzaReadyEvent() {
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}
