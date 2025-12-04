package be.uantwerpen.sd.project.model.domain;

import java.util.List;

public abstract class MealComponent {
    protected String name;

    public MealComponent(String name) {
        this.name = name;
    }

    //default throwing exception -> otherwise Ingredient will be able to add a mealc and its not supposed to be able to do that...
    public void add(MealComponent c) {
        throw new UnsupportedOperationException();
    }

    //idem
    public void remove(MealComponent c){
        throw new UnsupportedOperationException();
    }

    //get all leaf nodes
    public abstract List<Ingredient> getIngredients();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
