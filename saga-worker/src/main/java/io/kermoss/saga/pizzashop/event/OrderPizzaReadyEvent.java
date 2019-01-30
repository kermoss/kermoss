package io.kermoss.saga.pizzashop.event;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;

public class OrderPizzaReadyEvent extends BaseGlobalTransactionEvent {
    private String cartdId;

    @java.beans.ConstructorProperties({"cartdId"})
    public OrderPizzaReadyEvent(String cartdId) {
        this.cartdId = cartdId;
    }

    public OrderPizzaReadyEvent() {
    }

    public String getCartdId() {
        return this.cartdId;
    }
}
