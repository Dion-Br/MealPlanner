package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ingredient extends MealComponent {

    private double quantity;
    private Unit unit;
    private List<Tag> tags;

    public Ingredient(String name, double quantity, Unit unit, List<Tag> tags) {
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

    public Unit getUnit() {
        return unit;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Collections.singletonList(this);
    }

    @Override
    public String toString() {
        return String.format("Ingredient{name='%s', quantity=%.2f, unit='%s', tags=%s}",
                getName(), quantity, unit, tags);
    }
}