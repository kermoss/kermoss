package io.kermoss.saga.common.contract;

import java.util.List;

public class Need {
    private List<String> ingredient;

    public Need(List<String> ingredient) {
        this.ingredient = ingredient;
    }

    public Need() {
    }

    public List<String> getIngredient() {
        return ingredient;
    }

    public void setIngredient(List<String> ingredient) {
        this.ingredient = ingredient;
    }
}
