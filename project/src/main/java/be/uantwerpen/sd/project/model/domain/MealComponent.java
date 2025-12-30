package be.uantwerpen.sd.project.model.domain;

import java.util.List;

public abstract class MealComponent {

    protected String name;

    protected MealComponent(String name) {
        this.name = name;
    }

    public abstract List<Ingredient> getIngredients();

    public void add(MealComponent component) {
        throw new UnsupportedOperationException("Cannot add to leaf component");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}