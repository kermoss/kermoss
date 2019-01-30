package io.kermoss.saga.pizzashop.domain;

import java.util.List;
import java.util.UUID;

public class Cart {
    private String id = UUID.randomUUID().toString();
    private List<Pizza> pizzas;

    @java.beans.ConstructorProperties({"id", "pizzas"})
    public Cart(String id, List<Pizza> pizzas) {
        this.id = id;
        this.pizzas = pizzas;
    }

    public Cart() {
    }

    public String getId() {
        return this.id;
    }

    public List<Pizza> getPizzas() {
        return this.pizzas;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPizzas(List<Pizza> pizzas) {
        this.pizzas = pizzas;
    }
}
