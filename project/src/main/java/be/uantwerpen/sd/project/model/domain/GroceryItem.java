package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class GroceryItem {
    private String name;
    private double quantity;
    private Unit unit;
    private final BooleanProperty bought = new SimpleBooleanProperty(false);

    public GroceryItem(String name, double quantity, Unit unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public void addQuantity(double amount) {
        this.quantity += amount;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    // JavaFX Property for the checkbox
    public BooleanProperty boughtProperty() {
        return bought;
    }

    public boolean isBought() {
        return bought.get();
    }

    public void setBought(boolean bought) {
        this.bought.set(bought);
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
    }
}