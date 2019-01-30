package io.kermoss.saga.pizzashop.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import io.kermoss.saga.pizzashop.domain.Cart;
import io.kermoss.saga.pizzashop.event.OrderPizzaReceivedEvent;

@Service
public class PizzaService {
    ApplicationEventPublisher applicationEventPublisher;

    Map<String, Cart> orders;

    @Autowired
    public PizzaService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.orders = new HashMap<>();
    }

    @Transactional
    public void prepareOrder(Cart cart){
        orders.put(cart.getId(), cart);
        applicationEventPublisher.publishEvent(new OrderPizzaReceivedEvent(cart.getId()));
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return this.applicationEventPublisher;
    }

    public Map<String, Cart> getOrders() {
        return this.orders;
    }
}
