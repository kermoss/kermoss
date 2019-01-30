package io.kermoss.saga.pizzashop.domain;

import java.util.List;

public class Pizza {

    private final String name;
    private List<String> ingredients;
    private PizzaState pizzaState;

    @java.beans.ConstructorProperties({"name"})
    public Pizza(String name) {
        this.name = name;
    }

    @java.beans.ConstructorProperties({"name", "ingredients", "pizzaState"})
    public Pizza(String name, List<String> ingredients, PizzaState pizzaState) {
        this.name = name;
        this.ingredients = ingredients;
        this.pizzaState = pizzaState;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIngredients() {
        return this.ingredients;
    }

    public PizzaState getPizzaState() {
        return this.pizzaState;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setPizzaState(PizzaState pizzaState) {
        this.pizzaState = pizzaState;
    }


    public enum PizzaState{
        AJINA,
        T7AMRAT,
        TABET,
        WAJDAT,
        T7AR9ET
    }
}
