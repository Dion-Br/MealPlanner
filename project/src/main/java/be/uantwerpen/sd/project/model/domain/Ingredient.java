package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Ingredient extends MealComponent{
    private double quantity;
    private Unit unit;
    private List<String> tags;

    public Ingredient(String name, double quantity, Unit unit, List<String> tags) {
        super(name);
        this.quantity = quantity;
        this.unit = unit;
        this.tags = new ArrayList<>(tags);
    }

    public Ingredient(String name, double quantity, Unit unit) {
        this(name, quantity, unit, new ArrayList<>());
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() { return unit; }

    public void setUnit(Unit unit) { this.unit = unit; }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

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
                ", tags=" + getTags() + '\'' +
                "} " + super.toString();
    }
}
