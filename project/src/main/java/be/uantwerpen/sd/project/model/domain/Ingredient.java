package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Ingredient extends MealComponent{
    private double quantity;
    private Unit unit;

    public Ingredient(String name, double quantity, Unit unit) {
        super(name);
        this.quantity = quantity;
        this.unit = unit;
    }


    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() { return unit; }

    public void setUnit(Unit unit) { this.unit = unit; }

    @Override
    public List<Ingredient> getIngredients() {
        return Collections.singletonList(this); //empty list with just this ingr
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                ", quantity=" + getQuantity() +
                ", unit='" + getUnit() + '\'' +
                ", name='" + getName() + '\'' +
                "} " + super.toString();
    }
}
