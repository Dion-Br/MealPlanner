package be.uantwerpen.sd.project.model.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Ingredient extends MealComponent{
    private double quantity;

    public Ingredient(String name, double quantity) {
        super(name);
        this.quantity = quantity;
    }


    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Collections.singletonList(this); //empty list with just this ingr
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                ", quantity=" + getQuantity() +
                ", name='" + getName() + '\'' +
                ", name='" + getName() + '\'' +
                "} " + super.toString();
    }
}
