package be.uantwerpen.sd.project.model.domain;

import be.uantwerpen.sd.project.model.domain.enums.Unit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GroceryItem {

    private final String name;
    private final double quantity;
    private final Unit unit;
    private boolean bought;
    private final PropertyChangeSupport propertyChangeSupport;

    public GroceryItem(String name, double quantity, Unit unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.bought = false;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
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

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        boolean oldValue = this.bought;
        this.bought = bought;
        propertyChangeSupport.firePropertyChange("bought", oldValue, this.bought);
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f %s)", name, quantity, unit);
    }
}