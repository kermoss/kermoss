package io.kermoss.saga.pizzashop.event;

import io.kermoss.bfm.event.BaseLocalTransactionEvent;

public class BakingPizzaPendingEvent extends BaseLocalTransactionEvent {
    String cartId;

    public BakingPizzaPendingEvent(String cartId) {
        this.cartId = cartId;
    }

    public BakingPizzaPendingEvent() {
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

}
